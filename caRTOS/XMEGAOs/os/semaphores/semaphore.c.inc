// This file is the implementation for either the normal
// or '_n' semaphores. It will be included twice by
// semaphore.c to define both implementations.

// We have some declarations that make our editor happy. That way
// syntax highlighting etc will work.
#ifndef INCLUDED_FROM_SEMAPHORE_C
#	include "semaphore.h"
#	include "internal/Os_Kernel.h"
#	include "internal/Os_Error.h"
#	include <avr/interrupt.h>

#	define SEMAPHORE_MODE_N
#endif

#ifdef SEMAPHORE_MODE_NORMAL
#		define SEM_FUNC(name) name
#		define SEMAPHORE_T Semaphore
#		define N 1
#		define N_OF_ENTRY(q) 1
#		define PID_OF_ENTRY(q) (q)
#		define SET_QUEUE_ENTRY(q_entry, pid_value, n_value) \
			{ (q_entry) = (pid_value); }
#		define MAYBE_ARG_N
#		define MAYBE_ARG_N_VALUE
#		define COPY_ENTRY_FROM_TO(src, dst) { (dst) = (src); }
#		define MAYBE_RETURN_TYPE_FOR_N void
#else
#	ifdef SEMAPHORE_MODE_N
#		define SEM_FUNC(name) name##_n
#		define SEMAPHORE_T Semaphore_n
#		define N n
#		define N_OF_ENTRY(q) ((q).n)
#		define PID_OF_ENTRY(q) ((q).pid)
#		define SET_QUEUE_ENTRY(q_entry, pid_value, n_value)		\
			{ 													\
				(q_entry).pid = (pid_value);					\
				(q_entry).n   = (n_value);						\
			}
#		define MAYBE_ARG_N , uint8_t n
#		define MAYBE_ARG_N_VALUE , n
#		define COPY_ENTRY_FROM_TO(src, dst) { (dst).pid = (src).pid; (dst).n = (src).n; }
#		define MAYBE_RETURN_TYPE_FOR_N uint8_t
#	else
#		error You must define a mode.
#	endif
#endif

// return (x+1), but wrap around for semaphore waiting queue length
inline static int8_t SEM_FUNC(inc_wrapping)(SEMAPHORE_T* sem, int8_t x) {
	++x;
	if (x == sem->queue_cap)
		x = 0;
	return x;
}

// return (x-1), but wrap around for semaphore waiting queue length
inline static int8_t SEM_FUNC(dec_wrapping)(SEMAPHORE_T* sem, int8_t x) {
	if (x == 0)
		return sem->queue_cap - 1;
	else
		return x-1;
}


// put token or task id at the end of the waiting queue
static void SEM_FUNC(_sem_enqueue)(SEMAPHORE_T* sem, sem_token_t t MAYBE_ARG_N) {
	int8_t temp = SEM_FUNC(inc_wrapping)(sem, sem->queue_end);
	if (temp == sem->queue_front) {
		OS_report_fatal(OS_ERROR_SEM_QUEUE_FULL);
	}

	SET_QUEUE_ENTRY(sem->queue[sem->queue_end], t, N);
	sem->queue_end = temp;
}

// is at least one task/token waiting for the semaphore?
inline static BOOL SEM_FUNC(_sem_not_empty)(SEMAPHORE_T* sem) {
	return sem->queue_end != sem->queue_front;
}

// is this a task id (true) or a token (false)
//TODO move to section that is only evaluated once
inline static BOOL isTaskID(sem_token_t t) {
	return t < OS_NUMBER_OF_TCBS;
}

/*	Semaphore synchronization for queues*/
/*	@brief	Performs wait for queue semaphore*/
void SEM_FUNC(_sem_wait)(SEMAPHORE_T* sem MAYBE_ARG_N) {
	OS_ENTER_CRITICAL();

	// We have a First-Come-First-Serve scenario here:
	// -> reserve the necessary or at least the available space.
	sem->count -= N;

	// Is the semaphore free?
	//TODO This was "<1" resp. "<n", but we are sure that "<0" is right. Nevertheless,
	//     it seemed to work quite well, although it should have blocked the
	//     critical section semaphore for the queue...
	if (sem->count < 0) {
		// not free -> we have to wait

		TaskType t;

		GetTaskID(&t);

		// store task in waiting queue
		SEM_FUNC(_sem_enqueue)(sem, t MAYBE_ARG_N_VALUE);

		// block this task since the semaphore is not free
		WaitTask(t);
		//TODO interrupts active? -> probably corruption of ready_count!

		// we disable interrupts here to avoid corruption (still in critical section)
		//TODO they shouldn't be enabled at all (even in WaitTask)
		//NOTE not using OS_ENTER_CRITICAL because that must only be used in pairs
		cli();
	} else
		sem->ready_count -= N;

	// If we got here, no blocking was necessary.
	OS_EXIT_CRITICAL();
}

void SEM_FUNC(_sem_signal)(SEMAPHORE_T* sem MAYBE_ARG_N) {
	OS_ENTER_CRITICAL();

	// increase counters
	sem->count += N;
	sem->ready_count += N;

	// Is there a task waiting for this resource?
	// And: Is there enough resource available for the waiting task?
	//TODO check more than the first task! (-> ready_count)
	//TODO we may have to wake up more than one task
	if (SEM_FUNC(_sem_not_empty)(sem)
			&& sem->ready_count >= N_OF_ENTRY(sem->queue[sem->queue_front])) {
		uint8_t tId = PID_OF_ENTRY(sem->queue[sem->queue_front]);
		uint8_t task_n = N_OF_ENTRY(sem->queue[sem->queue_front]);

		// Wake the task waiting for this semaphore:
		// TODO: Replace ActivateTask() with ImmediatelyResumeTask()
		//ActivateTask(sem->queue[sem->queue_front].pid);
		if (isTaskID(tId)) {
			// Increment pointer to next semaphore queue entry
			//TODO sem_remove(index) function that also changes ready_count
			//TODO may be in the middle, not at the front of the queue; but
			//     special case for front!
			sem->queue_front = SEM_FUNC(inc_wrapping)(sem, sem->queue_front);

			// decrease ready count for tokens because we use it for the task
			sem->ready_count -= task_n;

			// resume the task
			//TODO rename ResumeTask to SwitchToTask?
			//TODO build a OS_Yield function that switches to the next ready task
			ResumeTask(tId); //TODO ResumeTask -> ResumeTaskImmediately, use new function ResumeTask here
		}

	}

	OS_EXIT_CRITICAL();
}

sem_token_t SEM_FUNC(_sem_start_wait)(SEMAPHORE_T* sem MAYBE_ARG_N) {
	uint8_t token;

	OS_ENTER_CRITICAL();

	token = sem->token_count;
	sem->token_count++;
	if (sem->token_count == 0)
		sem->token_count = OS_NUMBER_OF_TCBS;

	sem->count -= N;
	SEM_FUNC(_sem_enqueue)(sem, token MAYBE_ARG_N_VALUE);
	
	OS_EXIT_CRITICAL();

	return token;
}

BOOL SEM_FUNC(_sem_continue_wait)(SEMAPHORE_T* sem, sem_token_t token) {
	uint8_t i, remaining_ready_count;
	BOOL ready;

	if (token < OS_NUMBER_OF_TCBS) {
		OS_report_error(OS_ERROR_SEM_INVALID_TOKEN);
		return FALSE;
	}

	OS_ENTER_CRITICAL();

	// search for the token, but only in the ready tokens
	i = sem->queue_front;
	remaining_ready_count = sem->ready_count;

	// while not end of queue and token is ready
	ready = FALSE;
	while (i != sem->queue_end && N_OF_ENTRY(sem->queue[i]) <= remaining_ready_count) {
		// that amount of "readiness" is not available for the other tokens
		remaining_ready_count -= N_OF_ENTRY(sem->queue[i]);

		// is this the token we are looking for?
		if (PID_OF_ENTRY(sem->queue[i]) == token) {
			ready = TRUE;
			break;
		}

		i = SEM_FUNC(inc_wrapping)(sem,i);
	}

	//TODO if extra error checking is enabled:
	//      If not ready, look for token in rest of queue and
	//      report error, if it isn't found (thus invalid token).

	OS_EXIT_CRITICAL();

	return ready;
}

// returns whether the token was ready and decrements ready_count, if it was
static BOOL SEM_FUNC(_sem_remove_id)(SEMAPHORE_T* sem, sem_token_t token, uint8_t* n_out) {
	//TODO Please find better names for i, j and k.
	uint8_t i, j, k, pos;
	uint8_t n;
	BOOL was_ready = FALSE;
	BOOL found = FALSE;

	if (token == 0) {
		OS_report_error(OS_ERROR_SEM_INVALID_TOKEN);
		return FALSE;
	}

	OS_ENTER_CRITICAL();

	if (PID_OF_ENTRY(sem->queue[sem->queue_front]) == token) {
		// special case for first token in queue

		found = TRUE;

		n = N_OF_ENTRY(sem->queue[sem->queue_front]);

		was_ready = (n <= sem->ready_count);

		// remove token by incrementing queue_front
		sem->queue_front = SEM_FUNC(inc_wrapping)(sem, sem->queue_front);
	} else {
		// general case

		//TODO This cannot be used to determine whether the token is ready.
		//     => Make a function that can be used by this function and also
		//        continue_wait: BOOL find_token(token, out index, out ready, BOOL only_ready)
		i = sem->queue_front;
		while (i != sem->queue_end) {
			// have we found the ID that we want to remove?
			if (PID_OF_ENTRY(sem->queue[i]) == token) {
				// got it -> remove
				found = TRUE;

				//NOTE(Benjamin): We have two options here:
				// a) Move all items after i to the left.
				// b) Move all items before i to the right.
				// Here, we implement option (a), but in practice the token
				// will be near the front of the queue in most cases, so
				// option (b) would be faster.
				//TODO implement both of them and choose the one that is
				//     faster (depends on the position of the token, can
				//     be determined at runtime)

				//NOTE(Benjamin): This will fail, if i has wrapped around the end!!!
				//                And it obviously doesn't work for "_n".
				pos = i - sem->queue_front;
				was_ready = (pos < sem->ready_count);

				// store n because we need it later
				n = N_OF_ENTRY(sem->queue[i]);

				// move all items towards the front
				k = j = i;
				while (k != sem->queue_end) {
					k = SEM_FUNC(inc_wrapping)(sem, j);

					COPY_ENTRY_FROM_TO(sem->queue[k], sem->queue[j]);
					j = k;
				}

				// decrement queue
				sem->queue_end = SEM_FUNC(dec_wrapping)(sem, sem->queue_end);
			}

			i = SEM_FUNC(inc_wrapping)(sem, i);
		}
	}
	
	if (was_ready)
	{
		sem->ready_count -= N;
	}

	OS_EXIT_CRITICAL();

	*n_out = N;

	if (!found) {
		OS_report_error(OS_ERROR_SEM_INVALID_TOKEN);
		n = 0;
	}

	return was_ready;
}

MAYBE_RETURN_TYPE_FOR_N SEM_FUNC(_sem_finish_wait)(SEMAPHORE_T* sem, sem_token_t token) {
	BOOL was_ready;
	uint8_t n;

	if (isTaskID(token)) {
		OS_report_error(OS_ERROR_SEM_INVALID_TOKEN);
#ifdef SEMAPHORE_MODE_N
		return 0;
#else
		return ;
#endif
	}

	was_ready = SEM_FUNC(_sem_remove_id)(sem, token, &n);
	if (was_ready == FALSE)
	{
		OS_report_error(OS_ERROR_NOT_READY);
	}

#ifdef SEMAPHORE_MODE_N
	return n;
#endif
}

void SEM_FUNC(_sem_abort_wait)(SEMAPHORE_T* sem, sem_token_t token) {
	BOOL was_ready;
	uint8_t n;

	if (isTaskID(token)) {
		OS_report_error(OS_ERROR_SEM_INVALID_TOKEN);
		return;
	}

	was_ready = SEM_FUNC(_sem_remove_id)(sem, token, &n);

	// We need to restore the value of ready_count. If the
	// token was ready, _sem_remove_id has already decremented
	// it. Otherwise, we need to do that now.
	//TODO The description matches the code ^^
	//      However, I'm not sure that it is correct. The call
	//      to signal will wake up a task, but this should only
	//      be done, if the token was ready. I think we don't
	//      have to change ready_count (except by sem_signal)
	//      and we shouldn't call sem_signal, if the token was
	//      not ready. In that case, we should manually
	//      increment count and not call sem_signal.
	//  => Before we change that: This is different from our
	//     previous ideas and plans. I think it is correct, but
	//     we should think about that in detail.
	//     If we change it, we should make sure that the spec
	//     is still right (it shouldn't mention such details,
	//     but we should check that).
	if (was_ready == FALSE)
	{
		sem->ready_count -= N;
	}
	SEM_FUNC(_sem_signal)(sem MAYBE_ARG_N_VALUE);
}

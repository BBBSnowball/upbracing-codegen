/*
 * semaphore.c
 */
#include "semaphore.h"
#include "internal/Os_Kernel.h"
#include "internal/Os_Error.h"
#include <avr/interrupt.h>

// return (x+1), but wrap around for semaphore waiting queue length
inline static int8_t inc_wrapping(Semaphore* sem, int8_t x) {
	++x;
	if (x == sem->queue_cap)
		x = 0;
	return x;
}
inline static int8_t inc_wrapping_n(Semaphore_n* sem, int8_t x) {
	++x;
	if (x == sem->queue_cap)
		x = 0;
	return x;
}

// return (x-1), but wrap around for semaphore waiting queue length
inline static int8_t dec_wrapping(Semaphore* sem, int8_t x) {
	if (x == 0)
		return sem->queue_cap - 1;
	else
		return x-1;
}
inline static int8_t dec_wrapping_n(Semaphore_n* sem, int8_t x) {
	if (x == 0)
		return sem->queue_cap - 1;
	else
		return x-1;
}


// put token or task id at the end of the waiting queue
static void _sem_enqueue(Semaphore* sem, sem_token_t t) {
	int8_t temp = inc_wrapping(sem, sem->queue_end);
	if (temp == sem->queue_front) {
		//(KRISHNA): Need to get out of critical section ?
		OS_report_fatal(OS_ERROR_SEM_QUEUE_FULL);
	}

	sem->queue[sem->queue_end] = t;
	sem->queue_end = temp;
}

// put token or task id at the end of the waiting queue
static void _sem_enqueue_n(Semaphore_n* sem, sem_token_t t, uint8_t n) {
	int8_t temp = inc_wrapping_n(sem, sem->queue_end);
	if (temp == sem->queue_front) {
		//(KRISHNA): Need to get out of critical section ?
		OS_report_fatal(OS_ERROR_SEM_QUEUE_FULL);
	}

	sem->queue[sem->queue_end].pid = t;
	sem->queue[sem->queue_end].n   = n;
	sem->queue_end = temp;
}

// is at least one task/token waiting for the semaphore?
inline static BOOL _sem_not_empty(Semaphore* sem) {
	return sem->queue_end != sem->queue_front;
}
inline static BOOL _sem_not_empty_n(Semaphore_n* sem) {
	return sem->queue_end != sem->queue_front;
}

// is this a task id (true) or a token (false)
inline static BOOL isTaskID(sem_token_t t) {
	return t < OS_NUMBER_OF_TCBS;
}

void _sem_wait(Semaphore* sem) {
	OS_ENTER_CRITICAL();

	sem->count--;

	//TODO This was "<1", but we are sure that "<0" is right. Nevertheless,
	//     it seemed to work quite well, although it should have blocked the
	//     critical section semaphore for the queue...
	if (sem->count < 0) {
		TaskType t;

		GetTaskID(&t);

		_sem_enqueue(sem, t);

		// Another operation is going on: wait
		WaitTask(t);
		//TODO interrupts active? -> probably corruption of ready_count!
		
		// we disable interrupts here to avoid corruption (still in critical section)
		//TODO they shouldn't be enabled at all (even in WaitTask)
		//NOTE not using OS_ENTER_CRITICAL because that must only be used in pairs
		cli();
	} else
		sem->ready_count--;

	OS_EXIT_CRITICAL();
}

void _sem_signal(Semaphore* sem) {
	OS_ENTER_CRITICAL();

	sem->count++;
	sem->ready_count++;

	// Resume task, if tasks are waiting AND the resource is free
	//TODO consider all ready tasks/tokens
	if (sem->queue_end != sem->queue_front && sem->ready_count) {
		uint8_t tId = sem->queue[sem->queue_front];

		if (isTaskID(tId)) {
			// Increment pointer to next semaphore queue entry
			sem->queue_front = inc_wrapping(sem, sem->queue_front);

			// decrease ready count for tokens because we use it for the task
			sem->ready_count--;

			// Wake the task waiting for this semaphore:
			// TODO: Replace ActivateTask() with ImmediatelyResumeTask()
			//ActivateTask(sem->queue[sem->queue_front].pid);
			//To Peer: Resume task only if the front of the queue contains task id.
			//			do not do anything, if it is token

			//TODO rename ResumeTask to SwitchToTask?
			//TODO build a OS_Yield function that switches to the next ready task
			ResumeTask(tId); //TODO ResumeTask -> ResumeTaskImmediately, use new function ResumeTask here
		}
	}

	OS_EXIT_CRITICAL();
}

/*	Semaphore synchronization for queues*/
/*	@brief	Performs wait for queue semaphore*/
void _sem_wait_n(Semaphore_n* sem, uint8_t n) {
	TaskType t;

	OS_ENTER_CRITICAL();

	// We have a First-Come-First-Serve scenario here:
	// -> reserve the necessary or at least the available space.
	sem->count -= n;

	// Is the semaphore free?
	//TODO here it was "<n"; see comment in _sem_wait
	if (sem->count < 0) {
		GetTaskID(&t);

		// No.
		// 1) Store task in this queue
		_sem_enqueue_n(sem, t, n);

		// 2) Block this task since there is not enough free space
		WaitTask(t);
		
		// we disable interrupts here to avoid corruption (still in critical section)
		//TODO they shouldn't be enabled at all (even in WaitTask)
		//NOTE not using OS_ENTER_CRITICAL because that must only be used in pairs
		cli();
	} else
		sem->ready_count -= n;

	// If we got here, no blocking was necessary.
	OS_EXIT_CRITICAL();
}

void _sem_signal_n(Semaphore_n* sem, uint8_t n) {
	OS_ENTER_CRITICAL();

	// Signalize free space:
	sem->count += n;
	sem->ready_count += n;

	// Is there a task waiting for this resource?
	// And: Is there enough resource available for the waiting task?
	//TODO check more than the first task! (-> ready_count)
	if (_sem_not_empty_n(sem)
			&& sem->ready_count >= sem->queue[sem->queue_front].n) {
		uint8_t tId = sem->queue[sem->queue_front].pid;
		uint8_t task_n = sem->queue[sem->queue_front].n;

		// Wake the task waiting for this semaphore:
		// TODO: Replace ActivateTask() with ImmediatelyResumeTask()
		//ActivateTask(sem->queue[sem->queue_front].pid);
		if (isTaskID(sem->queue[sem->queue_front].pid)) {
			// Increment pointer to next semaphore queue entry
			//TODO sem_remove(index) function that also changes ready_count
			//TODO may be in the middle, not at the front of the queue; but
			//     special case for front!
			sem->queue_front = inc_wrapping_n(sem, sem->queue_front);

			// decrease ready count for tokens because we use it for the task
			sem->ready_count -= task_n;

			// resume the task
			//TODO see commit in _sem_signal
			ResumeTask(tId);
		}

	}

	OS_EXIT_CRITICAL();
}

sem_token_t _sem_start_wait(Semaphore* sem) {
	uint8_t tok;

	OS_ENTER_CRITICAL();

	tok = sem->token_count;
	sem->token_count++;
	if (sem->token_count == 0)
		sem->token_count = OS_NUMBER_OF_TCBS;

	sem->count--;
	_sem_enqueue(sem,tok);
	
	OS_EXIT_CRITICAL();
	return tok;

}

BOOL _sem_continue_wait(Semaphore* sem, sem_token_t token) {
	uint8_t check,pos;

	if (token < OS_NUMBER_OF_TCBS) {
		OS_report_error(OS_ERROR_SEM_INVALID_TOKEN);
		return FALSE;
	}

	OS_ENTER_CRITICAL();

	check = sem->queue_front;
	pos = sem->queue_front + sem->ready_count;
	if (pos >= sem->queue_cap)
	{
		pos = pos - sem->queue_cap;
	}

	while (check != pos) {
		if (token == sem->queue[check] && sem->ready_count > 0) {
			OS_EXIT_CRITICAL();
			return TRUE;
		}

		check = inc_wrapping(sem,check);
	}

	OS_EXIT_CRITICAL();

	return FALSE;

}


static BOOL _sem_remove_id(Semaphore* sem, sem_token_t token) {
	//TODO Please find better names for i, j and k.
	uint8_t i, j, k, pos=0;
	BOOL was_ready = FALSE;
	BOOL found = FALSE;

	if (token == 0) {
		OS_report_error(OS_ERROR_SEM_INVALID_TOKEN);
		return;
	}

	OS_ENTER_CRITICAL();

	if (sem->queue[sem->queue_front] == token) {
		sem->queue_front = inc_wrapping(sem, sem->queue_front);
		sem->ready_count--;

		OS_EXIT_CRITICAL();
		return TRUE;
	}

	i = sem->queue_front;
	while (i != sem->queue_end) {
		// have we found the ID that we want to remove?
		if (sem->queue[i] == token) {
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
			//pos = i - sem->queue_front; -> no need for this as we increment the position everytime we increment i
			was_ready = (pos < sem->ready_count);

			// move all items towards the front
			k = j = i;
			while (k != sem->queue_end) {
				k = inc_wrapping(sem, j);

				sem->queue[j] = sem->queue[k];
				j = k;
			}
			
			// decrement queue
			sem->queue_end = dec_wrapping(sem, sem->queue_end);
		}
		pos++;
		i = inc_wrapping(sem,i);
	}
	
	if (was_ready)
	{
		sem->ready_count--;
	}

	OS_EXIT_CRITICAL();

	if (!found) {
		OS_report_error(OS_ERROR_SEM_INVALID_TOKEN);
	}

	return was_ready;
}

void _sem_finish_wait(Semaphore* sem, sem_token_t token) {
	BOOL was_ready;

	if (isTaskID(token)) {
		OS_report_error(OS_ERROR_SEM_INVALID_TOKEN);
		return ;
	}

	was_ready = _sem_remove_id(sem,token);
	if (was_ready == FALSE)
	{
		OS_report_error(OS_ERROR_NOT_READY);
	}
}

void _sem_abort_wait(Semaphore* sem, sem_token_t token) {
	BOOL was_ready;

	if (isTaskID(token)) {
		OS_report_error(OS_ERROR_SEM_INVALID_TOKEN);
		return;
	}

	was_ready = _sem_remove_id(sem,token);

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
		sem->ready_count--;
	}
	_sem_signal(sem);
}

//TODO I have updated the normal functions, but not the "_n" functions.

sem_token_t _sem_start_wait_n(Semaphore_n* sem, uint8_t n) {

	uint8_t tok = 0;
	OS_ENTER_CRITICAL();
	tok = sem->token_count++;
	if (sem->token_count == 0)
		sem->token_count = OS_NUMBER_OF_TCBS;

	
	sem->count -= n;
	_sem_enqueue_n(sem,tok,n);
	OS_EXIT_CRITICAL();

	return tok;
}

BOOL _sem_continue_wait_n(Semaphore_n* sem, sem_token_t token) {
	uint8_t check,pos;

	if (token < OS_NUMBER_OF_TCBS) {
		OS_report_error(OS_ERROR_SEM_INVALID_TOKEN);
		return FALSE;
	}


	OS_ENTER_CRITICAL();
	check = sem->queue_front;
	pos = sem->queue_front + sem->ready_count;
	if (pos >= sem->queue_cap)
	{
		pos = pos - sem->queue_cap;
	}
	
	while (check != pos) {
		if (token == sem->queue[check].pid && sem->ready_count > 0) {
			OS_EXIT_CRITICAL();
			return TRUE;
		}

		check = inc_wrapping_n(sem,check);
	}
	OS_EXIT_CRITICAL();
	return FALSE;
}

static BOOL _sem_remove_id_n(Semaphore_n* sem, uint8_t* n, sem_token_t token) {
	uint8_t i, j, k, tok, pos=0;
	BOOL found = FALSE;
	BOOL was_ready = FALSE;
	
	tok = token;

	if (tok == 0) {
		OS_report_error(OS_ERROR_SEM_INVALID_TOKEN);
		return;
	}

	OS_ENTER_CRITICAL();
	if (sem->queue[sem->queue_front].pid == tok) {
		
		sem->ready_count = sem->ready_count - sem->queue[sem->queue_front].n;
		*n = sem->queue[sem->queue_front].n;
		sem->queue_front = inc_wrapping_n(sem,sem->queue_front);
		OS_EXIT_CRITICAL();
		return TRUE;
	}
	
	i = sem->queue_front;
	while (i != sem->queue_end) {
		// have we found the ID that we want to remove?
		if (sem->queue[i].pid == tok) {
			// got it -> remove
			found = TRUE;
			*n = sem->queue[i].n;
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
			//pos = i - sem->queue_front; -> no need for this as we increment the position everytime we increment i
			was_ready = pos < sem->ready_count;
			
			// move all items towards the front
			k = j = i;
			while (k != sem->queue_end) {
				k = inc_wrapping_n(sem,j);
				

				sem->queue[j].pid = sem->queue[k].pid;
				sem->queue[j].n = sem->queue[k].n;
				j = k;
			}
			// decrement queue
			sem->queue_end = dec_wrapping_n(sem,sem->queue_end);
			

		}
		i = inc_wrapping_n(sem,i);
		pos++;
	}
	OS_EXIT_CRITICAL();
	
	if (was_ready == TRUE)
	{
		sem->ready_count = sem->ready_count - *n;
	}
	if (!found)
	{
		OS_report_error(OS_ERROR_SEM_INVALID_TOKEN);
	}
	return was_ready;
}

uint8_t _sem_finish_wait_n(Semaphore_n* sem, sem_token_t token)
{
	BOOL was_ready;
	uint8_t n;
	
	if (isTaskID(token)) {
		OS_report_error(OS_ERROR_SEM_INVALID_TOKEN);
		return ;
	}
	
	was_ready = _sem_remove_id_n(sem,&n,token);
	if (was_ready == FALSE)
	{
		OS_report_error(OS_ERROR_NOT_READY);
	}
	return n;
}

void _sem_abort_wait_n(Semaphore_n* sem, sem_token_t token) 
{
	uint8_t tok, n;
	BOOL was_ready;
	
	tok = token;
	if (tok == 0) {
		OS_report_error(OS_ERROR_SEM_INVALID_TOKEN);
		return;
	}

	was_ready = _sem_remove_id_n(sem, &n, token);
	
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
		sem->ready_count = sem->ready_count - n;
	}
	_sem_signal_n(sem,n);
}

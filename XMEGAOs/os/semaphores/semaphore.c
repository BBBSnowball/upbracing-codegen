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

// put token or task id at the end of the waiting queue
static void _sem_enqueue(Semaphore* sem, sem_token_t t) {
	int8_t temp = inc_wrapping(sem, sem->queue_end);
	if (temp == sem->queue_front) {
		OS_report_fatal(OS_ERROR_SEM_QUEUE_FULL);
	}

	sem->queue[sem->queue_end] = t;
	sem->queue_end = temp;
}

// put token or task id at the end of the waiting queue
static void _sem_enqueue_n(Semaphore_n* sem, sem_token_t t, uint8_t n) {
	int8_t temp = inc_wrapping_n(sem, sem->queue_end);
	if (temp == sem->queue_front) {
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
	uint8_t tok = 0;
	tok = sem->token_count;
	sem->token_count++;
	if (sem->token_count == 0)
		sem->token_count = OS_NUMBER_OF_TCBS;

	//TODO error, if queue is full
	
	

	OS_ENTER_CRITICAL();
	sem->count--;
	_sem_enqueue(sem,tok);
	
	
	OS_EXIT_CRITICAL();
	return tok;

}

BOOL _sem_continue_wait(Semaphore* sem, sem_token_t token) {
	uint8_t tok;
	uint8_t check;
	tok = token;
	if (tok < OS_NUMBER_OF_TCBS) {
		OS_report_error(OS_ERROR_SEM_INVALID_TOKEN);
		return FALSE;
	}

	check = sem->queue_front;

	while (check < sem->queue_front + sem->ready_count) {
		if (tok == sem->queue[check] && sem->ready_count > 0) {
			
			return TRUE;
		}
		check = inc_wrapping(sem,check);
		
	}

	return FALSE;

}

//TODO remove this function -> put code into 'static BOOL sem_remove_id(...)' which
//     returns whether the token was ready and decrements ready_count, if it was
static BOOL _sem_remove_id(Semaphore* sem, sem_token_t token) {
	uint8_t i, j, k, tok, pos;
	tok = token;
	BOOL check = FALSE;

	if (tok == 0) {
		OS_report_error(OS_ERROR_SEM_INVALID_TOKEN);
		return;
	}

	OS_ENTER_CRITICAL();
	if (sem->queue[sem->queue_front] == tok) {
		
		sem->queue_front = inc_wrapping(sem,sem->queue_front);
		sem->ready_count--;

		return TRUE;
	}
	OS_EXIT_CRITICAL();
	OS_ENTER_CRITICAL();
	i = sem->queue_front;
	while (i != sem->queue_end) {
		if (sem->queue[i] == tok) {
			pos = i - sem->queue_front;
			if (pos < sem->ready_count)
			{
				check = TRUE;
			}
			j = i;
			k = j;
			while (k != sem->queue_end) {
				k = j + 1;
				if (k == sem->queue_cap) {
					k = 0;
				}

				sem->queue[j] = sem->queue[k];
				j = k;
			}
			
			if (sem->queue_end == 0) {
				sem->queue_end = sem->queue_cap - 1;
			}else{sem->queue_end--;}
			

		}
		i = inc_wrapping(sem,i);
	}
	OS_EXIT_CRITICAL();
	
	if (check == TRUE)
	{
		sem->ready_count--;
	}
	return check;
}

void _sem_finish_wait(Semaphore* sem, sem_token_t token) {
	uint8_t i, j, k;
	BOOL check;

	if (isTaskID(token)) {
		OS_report_error(OS_ERROR_SEM_INVALID_TOKEN);
		return ;
	}

	check = _sem_remove_id(sem,token);
	if (check == FALSE)
	{
		OS_report_error(OS_ERROR_NOT_READY);
	}


}

void _sem_abort_wait(Semaphore* sem, sem_token_t token) {
	uint8_t tok;
	tok = token;
	BOOL check;

	if (tok == 0) {
		OS_report_error(OS_ERROR_SEM_INVALID_TOKEN);
		return;
	}

	check = _sem_remove_id(sem,token);
	if (check == FALSE)
	{
		sem->ready_count--;
	}
	_sem_signal(sem);
	

}

sem_token_t _sem_start_wait_n(Semaphore_n* sem, uint8_t n) {

	uint8_t tok = 0;
	tok = sem->token_count++;
	if (sem->token_count == 0)
		sem->token_count = OS_TASKTYPE_MAX;

	OS_ENTER_CRITICAL();
	sem->count -= n;
	if (sem->count < n) {

		sem->queue[sem->queue_end].pid = tok;
		sem->queue[sem->queue_end].n = n;

		sem->queue_end++;
		if (sem->queue_end == sem->queue_cap) {
			sem->queue_end = 0;
		}

	}

	OS_EXIT_CRITICAL();

	return tok;
}

BOOL _sem_continue_wait_n(Semaphore_n* sem, sem_token_t token) {
	uint8_t check;

	if (token == 0) {
		return TRUE;
	}

	OS_ENTER_CRITICAL();
	check = sem->queue[sem->queue_front].pid;
	OS_EXIT_CRITICAL();
	if (check == token) {
		return TRUE;
	}
	return FALSE;
}

static BOOL _sem_remove_id_n(Semaphore_n* sem, uint8_t* n, sem_token_t token) {
	uint8_t i, j, k, tok, pos;
	BOOL check = FALSE;
	
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
		
		return TRUE;
	}
	OS_EXIT_CRITICAL();
	OS_ENTER_CRITICAL();
	i = sem->queue_front;
	while (i != sem->queue_end) {
		if (sem->queue[i].pid == tok) {
			*n = sem->queue[i].n;
			pos = i - sem->queue_front;
			if (pos < sem->ready_count)
			{
				check = TRUE;
			}
			j = i;
			k = j;
			while (k != sem->queue_end) {
				k = j + 1;
				if (k == sem->queue_cap)
					k = 0;

				sem->queue[j].pid = sem->queue[k].pid;
				sem->queue[j].n = sem->queue[k].n;
				j = k;
			}
			if (sem->queue_end == 0) {
				sem->queue_end = sem->queue_cap - 1;
			} else {
				sem->queue_end--;
			}

		}
		i = inc_wrapping_n(sem,i);
	}
	OS_EXIT_CRITICAL();
	
	if (check == TRUE)
	{
		sem->ready_count = sem->ready_count - *n;
	}
	return check;
}

uint8_t _sem_finish_wait_n(Semaphore_n* sem, sem_token_t token)
{
	BOOL check;
	uint8_t n;
	
	if (isTaskID(token)) {
		OS_report_error(OS_ERROR_SEM_INVALID_TOKEN);
		return ;
	}
	
	check = _sem_remove_id_n(sem,&n,token);
	if (check == FALSE)
	{
		OS_report_error(OS_ERROR_NOT_READY);
	}
	return n;
}

void _sem_abort_wait_n(Semaphore_n* sem, sem_token_t token) 
{
	uint8_t tok, n;
	BOOL check;
	
	tok = token;
	if (tok == 0) {
		OS_report_error(OS_ERROR_SEM_INVALID_TOKEN);
		return;
	}

	check = _sem_remove_id_n(sem, &n, token);
	if (check == FALSE)
	{
		sem->ready_count = sem->ready_count - n;
	}
	_sem_signal_n(sem,n);
}

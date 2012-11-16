package de.upbracing.code_generation.tests.context;

public interface Result {
	String getStatus();
	String getMessage();
	boolean isSuccessful();
	
	int getSeverity();
	Result combineWith(Result result);
	
	public static class Helpers {
		public static Result combine(Result a, Result b) {
			if (a.getSeverity() >= b.getSeverity())
				return a;
			else
				return b;
		}
	}
	
	public static class Success implements Result {
		public static final Result instance = new Success();
		
		private Success() { }
		
		@Override
		public String getStatus() {
			return "success";
		}
		
		@Override
		public String getMessage() {
			return "success";
		}
		
		@Override
		public boolean isSuccessful() {
			return true;
		}
		
		@Override
		public Result combineWith(Result result) {
			return Helpers.combine(this, result);
		}
		
		@Override
		public int getSeverity() {
			return 0;
		}
	}
	
	public static class NotStarted implements Result {
		public static final Result instance = new NotStarted();
		
		private NotStarted() { }
		
		@Override
		public String getStatus() {
			return "not-started";
		}
		
		@Override
		public String getMessage() {
			return "not started";
		}
		
		@Override
		public boolean isSuccessful() {
			return false;
		}
		
		@Override
		public Result combineWith(Result result) {
			return Helpers.combine(this, result);
		}
		
		@Override
		public int getSeverity() {
			return 0;
		}
	}
	
	public static class Running implements Result {
		public static final Result instance = new Running();
		
		private Running() { }
		
		@Override
		public String getStatus() {
			return "running";
		}
		
		@Override
		public String getMessage() {
			return "running";
		}
		
		@Override
		public boolean isSuccessful() {
			return false;
		}
		
		@Override
		public Result combineWith(Result result) {
			return Helpers.combine(this, result);
		}
		
		@Override
		public int getSeverity() {
			return 2;
		}
	}
	
	public static abstract class ErrorOrFailure implements Result {
		private String message;
		
		public ErrorOrFailure(String message) {
			this.message = message;
		}

		@Override
		public boolean isSuccessful() {
			return false;
		}
		
		@Override
		public String getMessage() {
			return message;
		}
		
		protected abstract Result getEmptyResult();
		
		@Override
		public Result combineWith(Result result) {
			return Helpers.combine(this, result);
		}
	}
	
	public static class Failure extends ErrorOrFailure {
		public static final Result empty = new Failure("");
		
		public Failure(String message) {
			super(message);
		}

		@Override
		public String getStatus() {
			return "failed";
		}
		
		@Override
		public int getSeverity() {
			return 3;
		}
		
		@Override
		protected Result getEmptyResult() {
			return empty;
		}
	}
	
	public static class Error extends ErrorOrFailure {
		public static final Result empty = new Error();
		
		private Throwable exception;
		
		private Error() {
			super("");
		}
		
		public Error(Throwable exception) {
			super(exception.getMessage());
			
			this.exception = exception;
		}

		public Error(String message) {
			super(message);
			
			this.exception = null;
		}

		@Override
		public String getStatus() {
			return "error";
		}
		
		public Throwable getException() {
			return exception;
		}
		
		@Override
		public int getSeverity() {
			return 4;
		}
		
		@Override
		protected Result getEmptyResult() {
			return empty;
		}
	}
}

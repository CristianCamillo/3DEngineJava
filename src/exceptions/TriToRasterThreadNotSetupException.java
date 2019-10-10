package exceptions;

public class TriToRasterThreadNotSetupException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public TriToRasterThreadNotSetupException()
	{
		super("TriToRasterThread not setup.");
	}
}

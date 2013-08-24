package models.link;

public interface Link {
	public boolean destroy();

	public String getName();

	public String getHumanReadableToken();

	public String getHref();

	public boolean exists();
}

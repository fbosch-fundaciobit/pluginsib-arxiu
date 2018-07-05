package es.caib.plugins.arxiu.filesystem;

public class VersionResponse {
	
	private String managerVersion;
	private String version;
	
	
	public VersionResponse(String managerVersion, String version) {
		super();
		this.managerVersion = managerVersion;
		this.version = version;
	}
	
	public String getManagerVersion() {
		return managerVersion;
	}
	public void setManagerVersion(String managerVersion) {
		this.managerVersion = managerVersion;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
}

package exception;

public class ResourceNotFoundException extends RuntimeException {
    private String resourceType;
    private int resourceId;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceType, int resourceId) {
        super(String.format("%s with ID %d not found", resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getResourceType() {
        return resourceType;
    }

    public int getResourceId() {
        return resourceId;
    }
}
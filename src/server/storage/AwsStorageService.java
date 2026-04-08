package server.storage;

import common.interfaces.Storable;

public class AwsStorageService implements Storable {

    private String awsEndpoint;

    public AwsStorageService(String awsEndpoint) {

        this.awsEndpoint = awsEndpoint;

    }

    public void uploadData() {

        // TODO: Implement upload logic

    }

    public void downloadData() {

        // TODO: Implement download logic

    }

    @Override

    public void save() {

        // TODO: Implement save logic

    }

    @Override

    public void load() {

        // TODO: Implement load logic

    }

    public String getAwsEndpoint() {
        return awsEndpoint;
    }

    public void setAwsEndpoint(String awsEndpoint) {
        this.awsEndpoint = awsEndpoint;
    }

}
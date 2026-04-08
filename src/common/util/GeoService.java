package common.util;

public class GeoService {

    private String apiKey;

    public GeoService(String apiKey) {

        this.apiKey = apiKey;

    }

    public double[] getCoordinates() {

        // TODO: Implement get coordinates logic

        return new double[2];

    }

    public double calculateDistance() {

        // TODO: Implement distance calculation

        return 0.0;

    }

    public String getApiKey() { return apiKey; }

    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

}
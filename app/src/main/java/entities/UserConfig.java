package entities;

import java.io.Serializable;

/**
 * Created by Dani_ on 24/10/2017.
 */

public class UserConfig implements Serializable {

    private String apiUrl;

    public UserConfig(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
}

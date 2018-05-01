package com.fastjrun.codeg.bundle.common;

public class RestController extends CommonController {
    private RestService restService;
    
    public RestService getRestService() {
        return restService;
    }

    public void setRestService(RestService restService) {
        this.restService = restService;
    }
}

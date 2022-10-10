package com.self.common.lambda;

import com.self.common.utils.http.BaseHttpRequest;

public interface HttpRequestAction {

    void success(BaseHttpRequest request, final String response);

    void failed(BaseHttpRequest request, final String errmsg);

}

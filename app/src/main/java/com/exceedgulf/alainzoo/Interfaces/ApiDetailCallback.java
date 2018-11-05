package com.exceedgulf.alainzoo.Interfaces;

import java.util.ArrayList;

/**
 * Created by Paras Ghasadiya on 29/12/17.
 */

public interface ApiDetailCallback<T> {
    public void onSuccess(final T result);

    public void onFaild(final String message);
}

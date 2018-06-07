package com.autochips.quickbootmanager.filters;

import android.content.Context;

public interface Filter {

    public void prepare(Context ctx);
    public boolean allowAlive(Context ctx, String pack);
    public void done(Context ctx);
}

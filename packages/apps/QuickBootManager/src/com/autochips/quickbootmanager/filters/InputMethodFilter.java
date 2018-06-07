package com.autochips.quickbootmanager.filters;

import java.util.List;

import android.content.Context;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

public class InputMethodFilter implements Filter {
    private List<InputMethodInfo> mInput = null;

    @Override
    public boolean allowAlive(Context ctx, String pack) {
        for (InputMethodInfo info : mInput) {
            if (info.getPackageName().equals(pack))
                return true;
        }
        return false;
    }

    @Override
    public void prepare(Context ctx) {
        if (mInput == null) {
            InputMethodManager imm =
                    (InputMethodManager) ctx.getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            mInput = imm.getEnabledInputMethodList();
        }
    }

    @Override
    public void done(Context ctx) {
        mInput = null;
    }

}

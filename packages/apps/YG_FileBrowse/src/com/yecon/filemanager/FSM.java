package com.yecon.filemanager;

/**
 * Created by chenchu on 15-3-10.
 */
public class FSM {
    public static final int OP_QUERY = -1;
    public static final int OP_RESET = 0;
    public static final int OP_UNSELECTED= 1;
    public static final int OP_SELECTED = 2;
    public static final int OP_DELETE = 3;
    public static final int OP_COPY = 4;
    public static final int OP_CANCEL = 5;
    public static final int OP_PASTE = 6;
    public static final int OP_CUT = 7;

    public static final int STATE_NONE = 0;
    public static final int STATE_READY = 1;
    public static final int STATE_COPY = 2;
    public static final int STATE_CUT = 3;

    private static boolean isInFastForward = false;

    public static void setFastForward(boolean b) { isInFastForward = b;}

    public static boolean getFastForward() { return isInFastForward;}

    public FSM() {
        super();
        mState = new OnNone();
    }

    private State mState;

    public int request(int op) {
        return mState.handle(this, op);
    }

    private abstract class State {
        abstract int handle(FSM fsm, int op);
    }
    
    class OnCut extends State {
    	@Override
    	int handle(FSM fsm, int op) {
    		// TODO Auto-generated method stub
    		int result = STATE_CUT;
    		if (op == OP_CANCEL ) {
                if (isInFastForward) {
                    fsm.mState = new OnNone();
                    setFastForward(false);
                    result = STATE_NONE;
                } else {
                    fsm.mState = new OnReady();
                    result = STATE_READY;
                }
            } else if (op == OP_PASTE || op == OP_UNSELECTED) {
                fsm.mState = new OnNone();
                setFastForward(false);
                result = STATE_NONE;
            } else if (op == OP_SELECTED) {
                fsm.mState = new OnReady();
                setFastForward(false);
                result = STATE_READY;
            }
    		return result;
    	}
    }

    class OnCopy extends State {
        @Override
        int handle(FSM fsm, int op) {
            int result = STATE_COPY;
            if (op == OP_CANCEL ) {
                if (isInFastForward) {
                    fsm.mState = new OnNone();
                    setFastForward(false);
                    result = STATE_NONE;
                } else {
                    fsm.mState = new OnReady();
                    result = STATE_READY;
                }
            } else if (op == OP_PASTE || op == OP_UNSELECTED) {
                fsm.mState = new OnNone();
                setFastForward(false);
                result = STATE_NONE;
            } else if (op == OP_SELECTED) {
                fsm.mState = new OnReady();
                setFastForward(false);
                result = STATE_READY;
            }
            return result;
        }
    }

    class OnReady extends State {
        @Override
        int  handle(FSM fsm, int op) {
            int result = STATE_READY;
            if (op == OP_COPY) {
                fsm.mState = new OnCopy();
                result = STATE_COPY;
            } else if(op == OP_CUT) {
            	fsm.mState = new OnCut();
                result = STATE_CUT;
            }
            else if ( op == OP_UNSELECTED || op == OP_DELETE || op == OP_RESET) {
                fsm.mState = new OnNone();
                result = STATE_NONE;
            }
            return result;
        }
    }


    class OnNone extends State {
        @Override
        int handle(FSM fsm, int msg) {
            int result = STATE_NONE;
            if (msg == OP_SELECTED) {
                fsm.mState = new OnReady();
                result = STATE_READY;
            }
            return result;
        }
    }
}


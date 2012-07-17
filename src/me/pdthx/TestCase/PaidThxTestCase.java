package me.pdthx.TestCase;

import android.test.TouchUtils;
import android.widget.EditText;
import android.view.View;
import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

/**
 *  Android GUI Unit Testing.
 *
 *  @author Edward Mitchell
 *  @param <T> The activity class being tested.
 */
public class PaidThxTestCase<T extends Activity> extends ActivityInstrumentationTestCase2<T>
{

    @SuppressWarnings("unchecked")
    public PaidThxTestCase(Class<? extends Activity> activityClass)
    {
        super((Class<T>)activityClass);
        // TODO Auto-generated constructor stub
    }

    @SuppressWarnings("unchecked")
    public <E extends View> E getView(Class<? extends View> viewClass, int viewId) {

        E view = (E) getActivity().findViewById(viewId);

        if (viewClass.isInstance(view))
        {
            return view;
        }

        return null;
    }

    public boolean click(final View view)
    {
        if (view.isClickable())
        {
            TouchUtils.clickView(this, view);
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean enterText(final EditText view, final String string)
    {

        Runnable run = new Runnable() {

            @Override
            public void run()
            {
                synchronized (this) {
                    view.setText(string);
                    notifyAll();
                }
            }

        };



        synchronized(run) {
            try
            {
                getActivity().runOnUiThread(run);
                run.wait();
                return true;
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
        }

    }
}

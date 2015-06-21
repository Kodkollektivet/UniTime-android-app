package com.jotto.unitime;

/**
 * Created by johanrovala on 18/06/15.
 */
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by johanrovala on 18/06/15.
 */
public class FragmentC extends Fragment implements View.OnClickListener {

    private FragmentActivity myContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_c, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button courseBtn = (Button) myContext.findViewById(R.id.get_course_btn);
        courseBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        EditText editText = (EditText) myContext.findViewById(R.id.course_code_text);
        String courseCode = editText.getText().toString();
        Toast.makeText(myContext, "Button clicked and course code is " + courseCode, Toast.LENGTH_LONG).show();
    }

}
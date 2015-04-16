package me.weijun.nfchat;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;

/**
 * Created by mac on 15/4/16.
 */
public class MainFragment extends Fragment {
    EditText mUsernameEditText;
    EditText mMessageEditText;
    Button mSendButton;
    TextView mContentTextView;
    Button mLogoutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mUsernameEditText = (EditText)getActivity().findViewById(R.id.username_edittext);
        mMessageEditText = (EditText)getActivity().findViewById(R.id.message_edittext);
        mSendButton = (Button)getActivity().findViewById(R.id.send_button);
        mContentTextView = (TextView)getActivity().findViewById(R.id.content_textview);
        mLogoutButton = (Button)getActivity().findViewById(R.id.logout_button);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AVUser.getCurrentUser().logOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });

        String myUserName = AVUser.getCurrentUser().getUsername();
        mContentTextView.setText(myUserName);
    }
}

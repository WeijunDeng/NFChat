package me.weijun.nfchat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SignUpCallback;

import java.util.List;

/**
 * Created by mac on 15/4/15.
 */
public class LoginFragment extends Fragment {

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        return rootView;
    }

    public void showPasswordEditText(final String tagId){
        MyApplication.currentTagId = "";

        final ImageView loginImageView  = (ImageView)getActivity().findViewById(R.id.login_imageView);
        final TextView loginTextView = ((TextView)getActivity().findViewById(R.id.login_textView));
        final EditText passwordEditText = (EditText)getActivity().findViewById(R.id.password_editText);

//        loginImageView.setImageDrawable(getResources().getDrawable(R.drawable.credit_card_2));
        loginTextView.setText("正在查询这张卡的主人...");

        AVQuery<AVObject> query = AVQuery.getQuery("_User");
        query.whereEqualTo("username", tagId);
        query.findInBackground(new FindCallback<AVObject>() {
            public void done(List<AVObject> objects, AVException e) {
                MyApplication.currentTagId = tagId;
                if (e == null) {
                    if (objects != null && objects.size() == 0) {
                        loginTextView.setText("你将成为这张卡的主人");
                        passwordEditText.setHint("请输入密码注册");
                    }
                    else {
                        loginTextView.setText("如果你是这张卡的主人");
                        passwordEditText.setHint("请输入密码登录");
                    }
                    getActivity().findViewById(R.id.login_imageView).setVisibility(View.GONE);
                    passwordEditText.setVisibility(View.VISIBLE);
                    passwordEditText.setFocusable(true);
                    passwordEditText.setFocusableInTouchMode(true);
                    passwordEditText.requestFocus();
                    InputMethodManager inputManager =
                            (InputMethodManager)passwordEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(passwordEditText, 0);
                } else {
                    loginTextView.setText("查询出错，请检查网络");
                }
            }
        });


        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (charSequence.length() > 0 && MyApplication.currentTagId.length() > 0) {
                    getActivity().findViewById(R.id.login_go_button).setVisibility(View.VISIBLE);
                }
                else {
                    getActivity().findViewById(R.id.login_go_button).setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        getActivity().findViewById(R.id.login_go_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = ((EditText) getActivity().findViewById(R.id.password_editText)).getText().toString();
                if (password.length() == 0 || MyApplication.currentTagId.length() == 0) {
                    return;
                }
                AVUser currentUser = AVUser.getCurrentUser();
                if (currentUser != null) {
                    currentUser.logOut();
                }

                if (passwordEditText.getText().toString().contains("注册")) {
                    AVUser newUser = new AVUser();
                    newUser.setUsername(MyApplication.currentTagId);
                    newUser.setPassword(password);
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                MyUtils.Toast("注册成功");
                            }
                            else {
                                MyUtils.Toast("注册失败" + String.valueOf(e.getCode()));
                            }
                        }
                    });
                }
                else {
                    AVUser.logInInBackground(MyApplication.currentTagId, password, new LogInCallback<AVUser>() {
                        @Override
                        public void done(AVUser avUser, AVException e) {
                            if (e == null && avUser != null) {
                                MyUtils.Toast("登录成功");
                            } else {
                                MyUtils.Toast("登录失败" + String.valueOf(e.getCode()));
                            }
                        }
                    });
                }



            }
        });
    }
}
package me.weijun.nfchat;

import android.content.Context;
import android.content.Intent;
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

/**
 * Created by mac on 15/4/15.
 */
public class LoginFragment extends Fragment {

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        return rootView;
    }

    public void showPasswordEditText(final String tagId){

        final ImageView loginImageView  = (ImageView)getActivity().findViewById(R.id.login_imageView);
        final TextView loginTextView = ((TextView)getActivity().findViewById(R.id.login_textView));
        final EditText passwordEditText = (EditText)getActivity().findViewById(R.id.password_editText);

//        loginImageView.setImageDrawable(getResources().getDrawable(R.drawable.credit_card_2));
        loginTextView.setText("正在查询这张卡的主人...");
        NFUser.findUserByTagIdInBackground(tagId, new NFUser.NFUserCallBack() {
                    @Override
                    public void succeed(NFUser user) {
                        if (user != null) {
                            loginTextView.setText("如果你是这张卡的主人");
                            passwordEditText.setHint("请输入密码登录");
                        }
                        else {
                            loginTextView.setText("你将成为这张卡的主人");
                            passwordEditText.setHint("请输入密码注册");
                        }
                        loginImageView.setVisibility(View.GONE);
                        passwordEditText.setVisibility(View.VISIBLE);
                        passwordEditText.setFocusable(true);
                        passwordEditText.setFocusableInTouchMode(true);
                        passwordEditText.requestFocus();
                        InputMethodManager inputManager =
                                (InputMethodManager)passwordEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.showSoftInput(passwordEditText, 0);
                    }

                    @Override
                    public void fail(AVException e) {
                        loginTextView.setText("查询出错，请检查网络");
                    }
                });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (charSequence.length() > 0 && tagId.length() > 0) {
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
                final String password = ((EditText) getActivity().findViewById(R.id.password_editText)).getText().toString();
                if (password.length() == 0 || tagId.length() == 0) {
                    return;
                }
                NFUser currentUser = NFUser.getCurrentUser();
                if (currentUser != null) {
                    currentUser.logOut();
                }

                if (passwordEditText.getHint().toString().contains("注册")) {
                    register(tagId, password);
                }
                else {
                    login(tagId, password);
                }

            }
        });
    }

    private void register(final String username, final String password) {
        NFUser.register(username, password, new NFUser.NFUserCallBack() {
            @Override
            public void succeed(NFUser user) {
                MyUtils.Toast("注册成功");
                login(username, password);
            }

            @Override
            public void fail(AVException e) {
                MyUtils.Toast("注册失败" + String.valueOf(e.getCode()));
            }
        });
    }

    private void login(String username, String password) {
        NFUser.login(username, password, new NFUser.NFUserCallBack() {
            @Override
            public void succeed(NFUser user) {
                MyUtils.Toast("登录成功" + user.getUserId());
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }

            @Override
            public void fail(AVException e) {
                MyUtils.Toast("登录失败" + String.valueOf(e.getCode()));
            }
        });
    }
}
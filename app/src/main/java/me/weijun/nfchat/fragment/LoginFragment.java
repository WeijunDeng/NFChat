package me.weijun.nfchat.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;

import me.weijun.nfchat.MyUtils;
import me.weijun.nfchat.R;
import me.weijun.nfchat.activity.MainActivity;
import me.weijun.nfchat.model.NFUser;

/**
 * Created by mac on 15/4/15.
 *
 */
public class LoginFragment extends BaseFragment {
    private TextView loginTextView;
    private ImageView loginImageView;
    private EditText idEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button idButton;

    private String tagId;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        loginImageView  = (ImageView)rootView.findViewById(R.id.login_imageView);
        loginTextView = ((TextView)rootView.findViewById(R.id.login_textView));
        idEditText = (EditText)rootView.findViewById(R.id.id_editText);
        passwordEditText = (EditText)rootView.findViewById(R.id.password_editText);
        loginButton = (Button)rootView.findViewById(R.id.login_go_button);
        idButton = (Button)rootView.findViewById(R.id.login_id_button);
        idButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIdLoginVisibility();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String password = passwordEditText.getText().toString();
                if (password.length() == 0) {
                    return;
                }
                if (idEditText.getVisibility() == View.VISIBLE) {
                    String userIDString = idEditText.getText().toString();
                    int userId = 0;
                    try {
                        userId = Integer.parseInt(userIDString);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    if (userIDString.length() == 0 || userId <= 0) {
                        return;
                    }
                    login(userId, password);
                }
                else {
                    if (passwordEditText.getHint().toString().contains("注册")) {
                        register(tagId, password);
                    }
                    else {
                        login(tagId, password);
                    }
                }
            }
        });
        return rootView;
    }

    private void setDefaultVisibility() {
        loginTextView.setVisibility(View.VISIBLE);
        loginImageView.setVisibility(View.VISIBLE);
        idEditText.setVisibility(View.GONE);
        passwordEditText.setVisibility(View.GONE);
        loginButton.setVisibility(View.GONE);
        idButton.setVisibility(View.VISIBLE);
        passwordEditText.clearFocus();
        passwordEditText.setText("");
        if (getView() != null) {
            InputMethodManager inputManager =
                    (InputMethodManager)getView().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,InputMethodManager.HIDE_NOT_ALWAYS);
        }
        loginButton.setVisibility(View.GONE);
    }

    private void setTagLoginVisibility() {
        loginTextView.setVisibility(View.VISIBLE);
        loginImageView.setVisibility(View.GONE);
        idEditText.setVisibility(View.GONE);
        passwordEditText.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);
        idButton.setVisibility(View.VISIBLE);
        passwordEditText.setFocusable(true);
        passwordEditText.setFocusableInTouchMode(true);
        passwordEditText.requestFocus();
        if (getView() != null) {
            InputMethodManager inputManager =
                    (InputMethodManager)getView().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(passwordEditText, 0);
        }
    }

    private void setIdLoginVisibility() {
        loginTextView.setVisibility(View.VISIBLE);
        loginImageView.setVisibility(View.GONE);
        idEditText.setVisibility(View.VISIBLE);
        passwordEditText.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);
        idButton.setVisibility(View.GONE);
        idEditText.setText("");
        passwordEditText.setText("");
    }


    public void showPasswordEditText(final String tagId){
        this.tagId = tagId;
        setDefaultVisibility();
        loginImageView.setImageDrawable(getResources().getDrawable(R.drawable.credit_card_2));
        loginTextView.setText("正在查询这张卡的主人...");
        NFUser.findUserByTagIdInBackground(tagId, new NFUser.NFUserCallBack() {
                    @Override
                    public void succeed(NFUser user) {
                        if (user != null) {
                            loginTextView.setText("如果你是这张卡的主人\nID:" + user.getUserId());
                            passwordEditText.setHint("请输入密码登录");
                        }
                        else {
                            loginTextView.setText("你将成为这张卡的主人");
                            passwordEditText.setHint("请输入密码注册");
                        }
                        setTagLoginVisibility();
                    }

                    @Override
                    public void fail(AVException e) {
                        loginTextView.setText("查询出错，请检查网络");
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
                MyUtils.Toast("注册失败" + e.getCode() + e.getMessage());
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
                MyUtils.Toast("登录失败" + e.getCode() + e.getMessage());
            }
        });
    }

    private void login(int userId, String password) {
        NFUser.login(userId, password, new NFUser.NFUserCallBack() {
            @Override
            public void succeed(NFUser user) {
                MyUtils.Toast("登录成功" + user.getUserId());
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }

            @Override
            public void fail(AVException e) {
                MyUtils.Toast("登录失败" + e.getCode() + e.getMessage());
            }
        });
    }
}
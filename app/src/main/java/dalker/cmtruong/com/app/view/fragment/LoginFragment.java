package dalker.cmtruong.com.app.view.fragment;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import dalker.cmtruong.com.app.R;
import dalker.cmtruong.com.app.helper.PreferencesHelper;
import dalker.cmtruong.com.app.model.Login;
import dalker.cmtruong.com.app.model.User;
import dalker.cmtruong.com.app.view.activity.MainActivity;
import timber.log.Timber;

/**
 * @author davidetruong
 * @version 1.0
 * @since 2018 August, 20th
 */
public class LoginFragment extends Fragment {

    @BindView(R.id.login_id_et)
    EditText login;

    @BindView(R.id.password_et)
    EditText password;

    @BindView(R.id.login_bt)
    Button loginBt;

    @BindView(R.id.signup_bt)
    Button signupBt;

    @BindView(R.id.login_pb)
    ProgressBar mProgress;

    String data;
    String loginText;
    String passwordText;

    public LoginFragment() {
    }

    public static LoginFragment getInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.d("Open login fragment");
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        setRetainInstance(true);
        ButterKnife.bind(this, view);

        openSignUpForm();
        showData();
        logIn();
        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState == null) {

        } else {
            login.setText(savedInstanceState.getString(getString(R.string.login_state)));
            password.setText(savedInstanceState.getString(getString(R.string.password_state)));
        }
    }

    private void openSignUpForm() {
        signupBt.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.profile_container, SignUpFragment.getInstance())
                .addToBackStack(null)
                .commit());
    }

    private void logIn() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        loginBt.setOnClickListener(v -> {
            Login loginUser = null;
            if (isValidLogin(login.getText().toString(), password.getText().toString())) {
                loginUser = new Login(login.getText().toString(), password.getText().toString());
            }
            Gson gson = new Gson();
            String loginJson = gson.toJson(loginUser);
            Timber.d(loginJson);
            loadingData();
            DatabaseReference mDB = FirebaseDatabase.getInstance().getReference(getString(R.string.users));
            mDB.orderByChild(getString(R.string.m_login_user)).equalTo(login.getText().toString())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    User user = data.getValue(User.class);
                                    if (user.getLogin().getPassword().equals(password.getText().toString())) {
                                        Timber.d("Verify : " + data.getKey() + " ====> " + user.toString());
                                        PreferencesHelper.saveUserSession(getContext(), user.toString());
                                        PreferencesHelper.saveDocumentReference(getContext(), user.getIdUser());
                                        int key = 3;
                                        Intent intent = new Intent(getContext(), MainActivity.class);
                                        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle();
                                        bundle.putInt(getString(R.string.transition_key), key);
                                        startActivity(intent, bundle);
                                        Snackbar.make(getView(),
                                                getString(R.string.welcome) + login.getText().toString(), Snackbar.LENGTH_SHORT).show();
                                    } else {
                                        Snackbar.make(getView(),
                                                R.string.login_failed, Snackbar.LENGTH_SHORT).show();
                                        showData();
                                    }

                                }
                            } else {
                                Snackbar.make(getView(),
                                        R.string.login_failed, Snackbar.LENGTH_SHORT).show();
                                showData();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        });
    }

    private boolean isValidLogin(String id, String password) {
        return !id.isEmpty() && !password.isEmpty();
    }

    private void loadingData() {
        mProgress.setVisibility(View.VISIBLE);
    }

    private void showData() {
        mProgress.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        loginText = login.getText().toString();
        passwordText = password.getText().toString();
        if (outState != null) {
            outState.putString(getString(R.string.login_state), loginText);
            outState.putString(getString(R.string.password_state), passwordText);
        }

    }
}

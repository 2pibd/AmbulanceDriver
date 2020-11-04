package com.twopibd.dactarbari.ambulance.drivers.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.twopibd.dactarbari.ambulance.drivers.R;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.tv_wait)
    TextView tv_wait;
    @BindView(R.id.ed_number)
    EditText ed_number;
    @BindView(R.id.ed_code_number)
    EditText ed_code_number;
    @BindView(R.id.relativeEnterNumberVerification)
    RelativeLayout relativeEnterNumberVerification;
    @BindView(R.id.relativeGetStarted)
    RelativeLayout relativeGetStarted;
    @BindView(R.id.backIcon_enter_phone)
    ImageView backIcon_enter_phone;
    @BindView(R.id.relativeotpVerification)
    RelativeLayout relativeotpVerification;
    @BindView(R.id.backIcon_enter_otp)
    ImageView backIcon_enter_otp;
    @BindView(R.id.cardVerifyOTP)
    CardView cardVerifyOTP;
    @BindView(R.id.cardVerifyNumber)
    CardView cardVerifyNumber;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    FirebaseAuth firebaseAuth;
    String mVerificationId;
    String VERIFICATION_PHONE_NUMBER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(getBaseContext(), MapsActivity.class));
            finish();
        } else {

            relativeotpVerification.animate().translationX(relativeotpVerification.getWidth());
            relativeEnterNumberVerification.animate().translationX(relativeotpVerification.getWidth());
            firebaseAuth = FirebaseAuth.getInstance();
        }

        hideKeyboard(this);

    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public void compare_pin(View view) {

        if (ed_code_number.getText().toString().trim().length() > 3) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, ed_code_number.getText().toString().trim());
            signInWithPhoneAuthCredential(credential);
        }
    }

    private void request_sms_varification(String phoneNumber) {
        //   progressDial.show();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {


                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                    Toast.makeText(LoginActivity.this, "Invalid request", Toast.LENGTH_LONG).show();

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Toast.makeText(LoginActivity.this, "The SMS quota for the project has been exceeded", Toast.LENGTH_LONG).show();

                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                tv_wait.setText("A pin is sending to " + phoneNumber + " Enter The Varification Code You Have Received Through SMS");

                VERIFICATION_PHONE_NUMBER = phoneNumber;

                //new code
              //  relativeotpVerification.animate().translationX(0);
               // relativeEnterNumberVerification.animate().translationX(-relativeEnterNumberVerification.getWidth());
                setCustomAnimationLeft(relativeEnterNumberVerification);


            }
        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber(


                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {
                        final FirebaseUser user = task.getResult().getUser();

                        // presentActivity();

                        //all done
                        // Intent i = new Intent(PhonVarificationActivity.this, SignUpActivity.class);
                        //   i.putExtra("code", credential.getSmsCode());
                        //  i.putExtra("number", ed_number.getText().toString().trim());
                        // i.putExtra("fb_id", user.getUid());
                        // CachedData.CODE= credential.getSmsCode();
                        //  CachedData.NUMBER=ed_number.getText().toString().trim().replace("+","");
                        //  CachedData.fb_id=user.getUid();


                        // startActivity(i);
                        // finish();

                        Log.i("mkl", user.toString());
                        Log.i("mkl", user.getUid());

                        Toast.makeText(this, "Successfully verified", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getBaseContext(), MapsActivity.class));
                        finish();

                    } else {
                        //  Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            //  MyUtils.getInstance().buildSnackMessage(snackView,"Invalid code !!!",LENGTH_LONG).show();
                            // Toasty.error(Phone_Varification_Activity.this,)
                            //  Toasty.error(PhonVarificationActivity.this,"Invalid code !!!",Toast.LENGTH_LONG,true).show();
                        }
                        // pDialog.hide();
                    }
                });
    }

    @OnClick(R.id.cardGetStarted)
    public void openPhoneNumberPage() {
        //works fine
        // relativeGetStarted.animate().translationX(-relativeGetStarted.getWidth());
        //  relativeotpVerification.animate().translationX(0);


        setCustomAnimationLeft(relativeGetStarted);
    }

    private void setCustomAnimationLeft(RelativeLayout view) {
        Animation animation1 =
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_left);
        view.startAnimation(animation1);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void setCustomAnimationRight(RelativeLayout relativeGetStarted) {
        relativeGetStarted.setVisibility(View.VISIBLE);
        Animation animation1 =
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_right);
        relativeGetStarted.startAnimation(animation1);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
               // LoginActivity.this.relativeGetStarted.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @OnClick(R.id.backIcon_enter_phone)
    public void openGetStarted() {
        //enter phone number back icon
        //works fine
       // relativeGetStarted.setVisibility(View.VISIBLE);
       // relativeGetStarted.animate().translationX(0);
      //  relativeotpVerification.animate().translationX(relativeotpVerification.getWidth());
        setCustomAnimationRight(relativeGetStarted);

    }

    @OnClick(R.id.backIcon_enter_otp)
    public void openEnterNumber() {
        //enter phone number back icon
        // relativeotpVerification.animate().translationX(relativeotpVerification.getWidth());
       // relativeEnterNumberVerification.animate().translationX(0);
        setCustomAnimationRight(relativeEnterNumberVerification);
    }

    @OnClick(R.id.cardVerifyNumber)
    public void openOtpVerify() {
        //enter phone number back icon
        //works fine
        String number = ed_number.getText().toString().trim();
        if (number.length() > 0) {
            request_sms_varification("+" + number);
        }
        //   relativeotpVerification.animate().translationX(0);
        //  relativeEnterNumberVerification.animate().translationX(-relativeEnterNumberVerification.getWidth());
    }
}

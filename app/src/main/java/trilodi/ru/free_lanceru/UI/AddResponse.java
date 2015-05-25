package trilodi.ru.free_lanceru.UI;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import trilodi.ru.free_lanceru.Components.BusProvider;
import trilodi.ru.free_lanceru.Components.UpdateProjectEvent;
import trilodi.ru.free_lanceru.Config;
import trilodi.ru.free_lanceru.Network.NetManager;
import trilodi.ru.free_lanceru.R;

public class AddResponse extends ActionBarActivity {

    ImageView backButton;

    LinearLayout time_check, valutes_changer;

    TextView dayer, val;

    String term="";
    String termType="3";

    String budgetS="";
    String CurrencyType="2";

    EditText budget,termE, commentE;

    RadioButton radio_usd, radio_esuro, radio_rub;

    Switch oly_cutomerB;

    String only_customer="0";
    String comment="";

    AlertDialog dialog, messageDialog;
    View loadingView, messageView;

    TextView messageTitle, messageText;

    ImageView OK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_response);

        OK = (ImageView) findViewById(R.id.OK);

        dayer = (TextView) findViewById(R.id.textView14);
        val = (TextView)findViewById(R.id.textView10);

        budget=(EditText)findViewById(R.id.editText2);
        termE=(EditText)findViewById(R.id.editText4);
        commentE=(EditText)findViewById(R.id.editText);

        loadingView = getLayoutInflater().inflate(R.layout.load_dialog_layout, null);
        messageView = getLayoutInflater().inflate(R.layout.message_dialog_layout, null);
        ProgressBarCircularIndeterminate progresser = (ProgressBarCircularIndeterminate) loadingView.findViewById(R.id.dialogProgress);

        TextView dialogTitle = (TextView) loadingView.findViewById(R.id.dialogtitle);
        TextView dialogDescription = (TextView) loadingView.findViewById(R.id.dialogDescription);

        dialogTitle.setText(getResources().getString(R.string.ADD_RESPONSE_DIALOG_TITLE));
        dialogDescription.setText(getResources().getString(R.string.ADD_RESPONSE_DIALOG_TEXT));


        messageTitle = (TextView) messageView.findViewById(R.id.dialogtitle);
        messageText = (TextView) messageView.findViewById(R.id.dialogDescription);

        dialog = new AlertDialog.Builder(this).setView(loadingView).setCancelable(false).create();

        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResponse();
            }
        });

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResponse();
            }
        });

        backButton = (ImageView) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        time_check = (LinearLayout) findViewById(R.id.timeclicker);
        valutes_changer = (LinearLayout) findViewById(R.id.valutesclicker);

        oly_cutomerB=(Switch)findViewById(R.id.switch1);

        oly_cutomerB.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

                if(isChecked){
                    only_customer="1";
                }else{
                    only_customer="0";
                }

            }
        });

        valutes_changer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(AddResponse.this);
                dialog.setContentView(R.layout.dialog_valute_layout);
                dialog.setTitle("Выберите валюту");
                dialog.setCancelable(true);


                radio_usd=(RadioButton)dialog.findViewById(R.id.usd);
                radio_esuro=(RadioButton)dialog.findViewById(R.id.euro);
                radio_rub=(RadioButton)dialog.findViewById(R.id.rub);


                String nval=val.getText().toString();

                if(nval.equals("USD")){
                    radio_usd.setChecked(true);
                }
                if(nval.equals("EURO")){
                    radio_esuro.setChecked(true);
                }
                if(nval.equals("РУБЛЬ")){
                    radio_rub.setChecked(true);
                }


                // there are a lot of settings, for dialog, check them all out!
                // set up radiobutton
                //RadioButton rd1 = (RadioButton) dialog.findViewById(R.id.rd_);
                //RadioButton rd2 = (RadioButton) dialog.findViewById(R.id.rd_2);

                View.OnClickListener CHANGER_VAL=new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RadioButton rb = (RadioButton) v;
                        switch (rb.getId()) {
                            case R.id.usd:
                                val.setText("USD");
                                CurrencyType="0";
                                break;
                            case R.id.euro:
                                val.setText("EURO");
                                CurrencyType="1";
                                break;
                            case R.id.rub:
                                val.setText("РУБЛЬ");
                                CurrencyType="2";
                                break;

                            default:
                                val.setText("РУБЛЬ");
                                CurrencyType="2";
                                break;
                        }
                        dialog.dismiss();
                    }
                };



                radio_usd.setOnClickListener(CHANGER_VAL);
                radio_esuro.setOnClickListener(CHANGER_VAL);
                radio_rub.setOnClickListener(CHANGER_VAL);

                // now that the dialog is set up, it's time to show it
                dialog.show();
            }
        });

        time_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(AddResponse.this);
                dialog.setContentView(R.layout.dialog_srok_layout);
                dialog.setTitle("Срок исполнения");
                dialog.setCancelable(true);


                RadioButton radio_chas=(RadioButton)dialog.findViewById(R.id.chas);
                RadioButton radio_den=(RadioButton)dialog.findViewById(R.id.den);
                RadioButton radio_mesyac=(RadioButton)dialog.findViewById(R.id.mesyac);
                RadioButton radio_project=(RadioButton)dialog.findViewById(R.id.project);


                String nval=dayer.getText().toString();

                if(nval.equals("Час")){
                    radio_chas.setChecked(true);
                }
                if(nval.equals("День")){
                    radio_den.setChecked(true);
                }
                if(nval.equals("Месяц")){
                    radio_mesyac.setChecked(true);
                }
                if(nval.equals("Проект")){
                    radio_project.setChecked(true);
                }


                // there are a lot of settings, for dialog, check them all out!
                // set up radiobutton
                //RadioButton rd1 = (RadioButton) dialog.findViewById(R.id.rd_);
                //RadioButton rd2 = (RadioButton) dialog.findViewById(R.id.rd_2);

                View.OnClickListener CHANGER_VAL=new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RadioButton rb = (RadioButton) v;
                        switch (rb.getId()) {
                            case R.id.chas:
                                dayer.setText("Час");
                                termType="0";
                                break;
                            case R.id.den:
                                dayer.setText("День");
                                termType="1";
                                break;
                            case R.id.mesyac:
                                dayer.setText("Месяц");
                                termType="2";
                                break;

                            case R.id.project:
                                dayer.setText("Проект");
                                termType="3";
                                break;

                            default:
                                dayer.setText("Проект");
                                termType="3";
                                break;
                        }
                        dialog.dismiss();
                    }
                };



                radio_chas.setOnClickListener(CHANGER_VAL);
                radio_den.setOnClickListener(CHANGER_VAL);
                radio_mesyac.setOnClickListener(CHANGER_VAL);
                radio_project.setOnClickListener(CHANGER_VAL);

                // now that the dialog is set up, it's time to show it
                dialog.show();
            }
        });

        budgetS=budget.getText().toString();
        comment=commentE.getText().toString();
        term=termE.getText().toString();
    }

    private void sendResponse(){
        dialog.show();

        budgetS=budget.getText().toString();
        comment=commentE.getText().toString();
        term=termE.getText().toString();

        comment+="\n<br /> (Отправлено из приложения https://play.google.com/store/apps/details?id=trilodi.ru.free_lance )";

        RequestParams localRequestParams = new RequestParams();
        localRequestParams.put("method", "projects_response_add");
        localRequestParams.put("project_id", Config.project_id);
        localRequestParams.put("comment", comment);
        localRequestParams.put("budget", budgetS);
        localRequestParams.put("currency", CurrencyType);
        localRequestParams.put("term", term);
        localRequestParams.put("term_dimension", termType);
        localRequestParams.put("only_customer", only_customer);

        NetManager.getInstance(AddResponse.this).post(localRequestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    try {
                        String str = new String(responseBody, "UTF-8");
                        System.out.println(str);

                        JSONObject resp = new JSONObject(str);

                        if (!resp.get("error").toString().equals("0")) {
                            messageTitle.setText("ОШИБКА!");
                            messageText.setText(resp.get("error_text").toString());
                            messageDialog = new AlertDialog.Builder(AddResponse.this).setView(messageView).setCancelable(true).create();
                            messageDialog.show();
                            //new MessagesDialog(ProjectActivity.this, "Ответ на проект", "Ошибка при отправки ответа. Ваша специализация не подходит под данный проект.").show();
                        } else if (resp.get("error").toString().equals("0")) {
                            BusProvider.getInstance().post(new UpdateProjectEvent());
                            AddResponse.this.finish();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                dialog.dismiss();
                super.onFinish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                messageTitle.setText("ОШИБКА!");
                messageText.setText("Во время ответа на проект произошла ошибка соединения.\nПроверьте соединение и повторите попытку.");
                messageDialog = new AlertDialog.Builder(AddResponse.this).setView(messageView).setCancelable(true).create();
                messageDialog.show();
                //new MessagesDialog(ProjectActivity.this, "Проект", "Во время ответа на проект произошла ошибка соединения.\nПроверьте соединение и повторите попытку.").show();
            }
        });
    }

}

package me.vipa.app.utils.recoSense;

import me.vipa.app.networking.apiendpoints.ApiInterface;
import me.vipa.app.networking.apiendpoints.RequestConfig;
import me.vipa.app.networking.errormodel.ApiErrorModel;
import me.vipa.app.utils.recoSense.bean.RecosenceResponse;
import me.vipa.app.MvHubPlusApplication;
import me.vipa.app.SDKConfig;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.cropImage.helpers.Logger;

import org.json.JSONObject;

import me.vipa.app.SDKConfig;
import me.vipa.app.networking.apiendpoints.ApiInterface;
import me.vipa.app.networking.apiendpoints.RequestConfig;
import me.vipa.app.networking.errormodel.ApiErrorModel;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.recoSense.bean.RecosenceResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecoSenceManager {

    private static RecoSenceManager recoSenceManagerInstance;
    private static ApiInterface endpoint;
    private RecoSenceManager() {

    }

    public static RecoSenceManager getInstance() {
        if (recoSenceManagerInstance == null) {
            endpoint = RequestConfig.getRecoClickClient().create(ApiInterface.class);
            recoSenceManagerInstance = new RecoSenceManager();
        }
        return (recoSenceManagerInstance);
    }


    public void sendClickEvent(String screenType, int id) {
        try {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("action_type",RecoSenceActionTypes.VIEW);
            jsonObject.put("app_ver","1");
            jsonObject.put("channel",RecoSenceActionTypes.CHANNEL);
            jsonObject.put("client_id","cpcqnahvyzlvclzr4wxa");


            JSONObject jsonObject1=new JSONObject();
            jsonObject1.put("language", SDKConfig.getInstance().getThaiLangCode());
            jsonObject.put("context",jsonObject1);

            jsonObject.put("device_id", AppCommonMethod.getDeviceId(MvHubPlusApplication.getInstance().getContentResolver()));
            jsonObject.put("item_id", String.valueOf(id));
            jsonObject.put("timestamp", AppCommonMethod.getCurrentTimeStamp());
            jsonObject.put("user_id", AppCommonMethod.getDeviceId(MvHubPlusApplication.getInstance().getContentResolver()));


            /*DateFormat df = DateFormat.getTimeInstance();
            df.setTimeZone(TimeZone.getTimeZone("gmt"));
            String gmtTime = df.format(new Date());
*/
            Logger.w("recoObject  ",jsonObject+"");

          //  Logger.w("recoObject---->>",gmtTime+"");
            endpoint.getRecoClick(jsonObject).enqueue(new Callback<RecosenceResponse>() {
                @Override
                public void onResponse(Call<RecosenceResponse> call, Response<RecosenceResponse> response) {
                    Logger.w("configResponse",response+"");
                    if (response.isSuccessful()) {


                    } else {

                    }

                }

                @Override
                public void onFailure(Call<RecosenceResponse> call, Throwable t) {
                    Logger.w("configResponse--",t.getMessage()+"");
                    ApiErrorModel errorModel = new ApiErrorModel(500, t.getMessage());

                }
            });


        }catch (Exception e){

        }
    }
}

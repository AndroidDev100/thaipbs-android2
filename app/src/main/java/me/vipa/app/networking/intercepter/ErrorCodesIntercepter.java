package me.vipa.app.networking.intercepter;


import me.vipa.app.MvHubPlusApplication;
import me.vipa.app.R;
import me.vipa.app.beanModel.emptyResponse.ResponseEmpty;
import me.vipa.app.beanModel.entitle.ResponseEntitle;
import me.vipa.app.beanModel.membershipAndPlan.ResponseMembershipAndPlan;
import me.vipa.app.beanModel.purchaseModel.PurchaseResponseModel;
import me.vipa.app.beanModel.responseGetWatchlist.ResponseGetIsWatchlist;
import me.vipa.app.beanModel.responseIsLike.ResponseIsLike;
import me.vipa.app.beanModel.responseModels.LoginResponse.LoginResponseModel;
import me.vipa.app.beanModel.responseModels.SignUp.SignupResponseAccessToken;
import me.vipa.app.beanModel.responseModels.listAllAccounts.AllSecondaryAccountDetails;
import me.vipa.app.beanModel.responseModels.secondaryUserDetails.SecondaryUserDetailsJavaPojo;
import me.vipa.app.beanModel.responseModels.switchUserDetail.SwitchUser;
import me.vipa.app.beanModel.userProfile.UserProfileResponse;
import me.vipa.app.redeemcoupon.RedeemCouponResponseModel;
import me.vipa.app.repository.redeemCoupon.RedeemModel;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.cropImage.helpers.Logger;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;

import org.json.JSONObject;

import me.vipa.bookmarking.bean.continuewatching.GetContinueWatchingBean;
import me.vipa.userManagement.bean.allSecondaryDetails.AllSecondaryDetails;
import me.vipa.userManagement.bean.allSecondaryDetails.SecondaryUserDetails;
import me.vipa.userManagement.bean.allSecondaryDetails.SwitchUserDetails;
import retrofit2.Response;

public class ErrorCodesIntercepter {

    private static ErrorCodesIntercepter instance;
    static MvHubPlusApplication appInstance;
    static int ErrorCode409 = 409;
    static int ErrorCode400 = 400;


    private ErrorCodesIntercepter() {
    }

    public static ErrorCodesIntercepter getInstance() {

        if (instance == null) {
            instance = new ErrorCodesIntercepter();
            appInstance = MvHubPlusApplication.getInstance();
        }
        if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("Thai") || KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("हिंदी")) {
            AppCommonMethod.updateLanguage("th", MvHubPlusApplication.getInstance());
        } else if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("English")) {
            AppCommonMethod.updateLanguage("en", MvHubPlusApplication.getInstance());
        }
        return (instance);
    }


    public SignupResponseAccessToken manualSignUp(Response<me.vipa.userManagement.bean.LoginResponse.LoginResponseModel> response) {
        SignupResponseAccessToken responseModel = null;
        if (response.code() == ErrorCode409) {
            LoginResponseModel cl = new LoginResponseModel();
            cl.setResponseCode(4901);
            String debugMessage = "";

            try {
                JSONObject jObjError = new JSONObject(response.errorBody().string());
                debugMessage = jObjError.getString("debugMessage");
                Logger.e("", "" + jObjError.getString("debugMessage"));

                responseModel = new SignupResponseAccessToken();
                if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("English")) {
                    responseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.user_already_exists));
                } else {
                    responseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.user_already_exists));
                }

            } catch (Exception e) {
                Logger.e("RegistrationLoginRepo", "" + e.toString());
            }


            responseModel.setResponseModel(cl);

        } else if (response.code() == ErrorCode400) {
            LoginResponseModel cl = new LoginResponseModel();
            cl.setResponseCode(400);
            responseModel = new SignupResponseAccessToken();
            try {
                JSONObject errorObject = new JSONObject(response.errorBody().string());
                if (errorObject.getInt("responseCode") != 0) {
                    if (errorObject.getInt("responseCode") == 4003) {
                        responseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.password_cannot_be_blank));

                    } else if (errorObject.getInt("responseCode") == 4004) {
                        responseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.please_provide_valid_name));

                    } else if (errorObject.getInt("responseCode") == 4005) {
                        responseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.email_id_cannot_be_blank));

                    }else if (errorObject.getInt("responseCode") == 4017) {
                        responseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.invalid_email_id));

                    } else {
                        responseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong));

                    }
                } else {
                    responseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong));

                }

                responseModel.setResponseModel(cl);


            } catch (Exception e) {
                responseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong));

                responseModel.setResponseModel(cl);
            }
        }
        return responseModel;
    }

    public LoginResponseModel fbLogin(Response<me.vipa.userManagement.bean.LoginResponse.LoginResponseModel> response) {
        LoginResponseModel cl = null;
        try {
            cl = new LoginResponseModel();
            JSONObject errorObject = new JSONObject(response.errorBody().string());

            if (errorObject.getInt("responseCode") != 0) {
                if (errorObject.getInt("responseCode") == 4301) {
                    cl.setResponseCode(403);
                    cl.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.either_user_email_not_found));

                } else if (errorObject.getInt("responseCode") == 4103) {
                    cl.setResponseCode(400);
                    cl.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.user_deactivated));

                } else if (errorObject.getInt("responseCode") == 4901) {
                    cl.setResponseCode(400);
                    cl.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.user_already_exists));

                } else if (errorObject.getInt("responseCode") == 4007) {
                    cl.setResponseCode(400);
                    cl.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.fb_id_cannot_be_null_or_empty));

                } else if (errorObject.getInt("responseCode") == 500) {
                    cl.setResponseCode(400);
                    cl.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));

                } else {
                    cl.setResponseCode(400);
                    cl.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));

                }
            }
        } catch (Exception e) {
            cl.setResponseCode(400);
            cl.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));
        }

        return cl;
    }

    public LoginResponseModel Login(Response<me.vipa.userManagement.bean.LoginResponse.LoginResponseModel> response) {
        LoginResponseModel responseModel = null;
        try {
            responseModel = new LoginResponseModel();
            JSONObject errorObject = new JSONObject(response.errorBody().string());
            responseModel.setResponseCode(400);
            if (errorObject.getInt("responseCode") != 0) {
                if (errorObject.getInt("responseCode") != 0) {
                    if (errorObject.getInt("responseCode") == 4003) {
                        responseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.password_cannot_be_blank));

                    } else if (errorObject.getInt("responseCode") == 4004) {
                        responseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.please_provide_valid_name));

                    } else if (errorObject.getInt("responseCode") == 4401) {
                        responseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.user_does_not_exists));

                    } else if (errorObject.getInt("responseCode") == 4103) {
                        responseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.user_deactivated));

                    } else if (errorObject.getInt("responseCode") == 4006) {
                        responseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.user_can_not_login));

                    } else if (errorObject.getInt("responseCode") == 4002) {
                        responseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.username_password_doest_match));

                    } else if (errorObject.getInt("responseCode") == 4005) {
                        responseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.email_id_cannot_be_blank));
                    } else {
                        responseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong));

                    }
                } else {
                    responseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong));
                }
            }
        } catch (Exception e) {

        }

        return responseModel;
    }

    public ResponseEntitle checkEntitlement(Response<ResponseEntitle> response) {
        ResponseEntitle responseEntitlement = new ResponseEntitle();
        try {

            JSONObject errorObject = new JSONObject(response.errorBody().string());
            if (errorObject.getInt("responseCode") != 0) {
                int code = errorObject.getInt("responseCode");
                if (code == 4406) {
                    responseEntitlement.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.no_valid_offer));
                    responseEntitlement.setStatus(false);
                } else if (code == 4001) {
                    responseEntitlement.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.no_Subscription_managment));
                    responseEntitlement.setStatus(false);
                } else if (code == 4302) {
                    responseEntitlement.setResponseCode(4302);
                    Logger.w("languageValeu-->>", MvHubPlusApplication.getInstance().getResources().getString(R.string.you_are_logged_out));
                    responseEntitlement.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.you_are_logged_out));
                } else if (code == 500) {
                    responseEntitlement.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.server_error));
                    responseEntitlement.setStatus(false);
                }

            } else {
                responseEntitlement.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));
                responseEntitlement.setStatus(false);
            }


        } catch (Exception ignored) {
            responseEntitlement.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));
            responseEntitlement.setStatus(false);
        }
        return responseEntitlement;
    }


    public ResponseMembershipAndPlan checkPlans(Response<ResponseMembershipAndPlan> response, ResponseMembershipAndPlan responseEntitlement) {
        return null;
    }

    public UserProfileResponse userProfile(Response<me.vipa.userManagement.bean.UserProfile.UserProfileResponse> response) {
        UserProfileResponse empty = new UserProfileResponse();
        try {
            JSONObject errorObject = new JSONObject(response.errorBody().string());
            if (errorObject.getInt("responseCode") != 0) {
                int code = errorObject.getInt("responseCode");
                if (code == 4302) {
                    empty.setStatus(false);
                    empty.setResponseCode(4302);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.you_are_logged_out));
                } else if (code == 500) {
                    empty.setStatus(false);
                    empty.setResponseCode(500);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));
                } else if (code == 4019){
                    empty.setStatus(false);
                    empty.setResponseCode(4019);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.number_Cannot_change));
                }else if (code == 4901){
                    empty.setStatus(false);
                    empty.setResponseCode(4901);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.already_exist_number));
                }
            }
        } catch (Exception ignored) {
            empty.setResponseCode(500);
            empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));
        }
        return empty;
    }

    public ResponseEmpty addToWatchlisht(Response<ResponseEmpty> response) {
        ResponseEmpty empty = new ResponseEmpty();
        try {
            JSONObject errorObject = new JSONObject(response.errorBody().string());
            if (errorObject.getInt("responseCode") != 0) {
                int code = errorObject.getInt("responseCode");
                if (code == 4904) {
                    empty.setStatus(false);
                    empty.setResponseCode(4904);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.user_already_in_watchlist));
                }if (code == 4408) {
                    empty.setStatus(false);
                    empty.setResponseCode(4408);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.user_not_already_in_watchlist));
                } else if (code == 4302) {
                    empty.setStatus(false);
                    empty.setResponseCode(4302);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.you_are_logged_out));
                } else if (code == 500) {
                    empty.setStatus(false);
                    empty.setResponseCode(500);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));
                }
            }
        } catch (Exception ignored) {
            empty.setResponseCode(500);
            empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));
        }
        return empty;
    }

    public RedeemModel redeemCoupon(Response<RedeemCouponResponseModel> response) {
        RedeemModel empty = new RedeemModel();
        try {
            JSONObject errorObject = new JSONObject(response.errorBody().string());
            if (errorObject.getInt("responseCode") != 0) {
                int code = errorObject.getInt("responseCode");
                if (code == 4903) {
                    empty.setStatus(false);
                    empty.setResponseCode(4903);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.subscribed_already));
                } else if (code == 4044) {
                    empty.setStatus(false);
                    empty.setResponseCode(4044);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.redeem_code_already_used));
                } else if (code == 4046) {
                    empty.setStatus(false);
                    empty.setResponseCode(4046);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.redeem_cancelled));
                } else if (code == 4045) {
                    empty.setStatus(false);
                    empty.setResponseCode(4045);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.redeem_code_invalid));
                } else if (code == 4047) {
                    empty.setStatus(false);
                    empty.setResponseCode(4047);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.redeem_code_expired));
                } else if (code == 4049) {
                    empty.setStatus(false);
                    empty.setResponseCode(4049);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.please_try));
                } else if (code == 4404) {
                    empty.setStatus(false);
                    empty.setResponseCode(4404);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.cannot_find_other_offer));
                } else if (code == 4405) {
                    empty.setStatus(false);
                    empty.setResponseCode(4405);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.not_find_any_offer));
                } else if (code == 4409) {
                    empty.setStatus(false);
                    empty.setResponseCode(4409);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.currency_not_supported));
                } else if (code == 4410) {
                    empty.setStatus(false);
                    empty.setResponseCode(4410);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.cannot_find_any_subs));
                }else if (code == 4302) {
                    empty.setStatus(false);
                    empty.setResponseCode(4302);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.you_are_logged_out));
                }
                else if (code == 500) {
                    empty.setStatus(false);
                    empty.setResponseCode(500);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));
                }
            }
        } catch (Exception ignored) {
            empty.setResponseCode(500);
            empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));
        }
        return empty;
    }

    public ResponseEmpty likeAsset(Response<ResponseEmpty> response) {
        ResponseEmpty empty = new ResponseEmpty();
        try {
            JSONObject errorObject = new JSONObject(response.errorBody().string());
            if (errorObject.getInt("responseCode") != 0) {
                int code = errorObject.getInt("responseCode");
                if (code == 4902) {
                    empty.setStatus(false);
                    empty.setResponseCode(4902);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.user_already_liked));
                }else if (code == 4403) {
                    empty.setStatus(false);
                    empty.setResponseCode(4403);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.user_already_liked));
                } else if (code == 4302) {
                    empty.setStatus(false);
                    empty.setResponseCode(4302);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.you_are_logged_out));
                } else if (code == 500) {
                    empty.setStatus(false);
                    empty.setResponseCode(500);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));
                }
            }
        } catch (Exception ignored) {
            empty.setResponseCode(500);
            empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));
        }
        return empty;
    }


    public ResponseGetIsWatchlist isWatchlist(Response<ResponseGetIsWatchlist> response) {
        ResponseGetIsWatchlist empty = new ResponseGetIsWatchlist();
        try {
            JSONObject errorObject = new JSONObject(response.errorBody().string());
            if (errorObject.getInt("responseCode") != 0) {
                int code = errorObject.getInt("responseCode");
                if (code == 4302) {
                    empty.setStatus(false);
                    empty.setResponseCode(4302);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.you_are_logged_out));
                } else if (code == 500) {
                    empty.setStatus(false);
                    empty.setResponseCode(500);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));
                }
            }
        } catch (Exception ignored) {
            empty.setResponseCode(500);
            empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));
        }
        return empty;
    }

    public ResponseIsLike isLike(Response<ResponseIsLike> response) {
        ResponseIsLike empty = new ResponseIsLike();
        try {
            JSONObject errorObject = new JSONObject(response.errorBody().string());
            if (errorObject.getInt("responseCode") != 0) {
                int code = errorObject.getInt("responseCode");
                if (code == 4302) {
                    empty.setStatus(false);
                    empty.setResponseCode(4302);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.you_are_logged_out));
                } else if (code == 500) {
                    empty.setStatus(false);
                    empty.setResponseCode(500);
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));
                }
            }
        } catch (Exception ignored) {
            empty.setResponseCode(500);
            empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));
        }
        return empty;
    }

    public PurchaseResponseModel createNewOrder(Response<PurchaseResponseModel> response) {
        PurchaseResponseModel purchaseResponseModel = new PurchaseResponseModel();
        try {

            JSONObject errorObject = new JSONObject(response.errorBody().string());
            if (errorObject.getInt("responseCode") != 0) {
                int code = errorObject.getInt("responseCode");
                if (code == 4414) {
                    purchaseResponseModel.setResponseCode(4414);
                    purchaseResponseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.payment_config_not_found));
                } else if (code == 4404) {
                    purchaseResponseModel.setResponseCode(4404);
                    purchaseResponseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.cannot_find_other_offer));
                } else if (code == 4405) {
                    purchaseResponseModel.setResponseCode(4405);
                    purchaseResponseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.not_find_any_offer));
                } else if (code == 4409) {
                    purchaseResponseModel.setResponseCode(4409);
                    purchaseResponseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.currency_not_supported));
                } else if (code == 4410) {
                    purchaseResponseModel.setResponseCode(4410);
                    purchaseResponseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.not_find_any_offer));
                } else if (code == 4302) {
                    purchaseResponseModel.setResponseCode(4302);
                    purchaseResponseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.you_are_logged_out));
                } else if (code == 500) {
                    purchaseResponseModel.setResponseCode(500);
                    purchaseResponseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));
                }
            }

        } catch (Exception ignored) {
            purchaseResponseModel.setResponseCode(500);
            purchaseResponseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));
        }
        return purchaseResponseModel;
    }

    public PurchaseResponseModel initiateOrder(Response<PurchaseResponseModel> response) {
        PurchaseResponseModel purchaseResponseModel = new PurchaseResponseModel();
        try {

            JSONObject errorObject = new JSONObject(response.errorBody().string());
            if (errorObject.getInt("responseCode") != 0) {
                int code = 4008;//errorObject.getInt("responseCode");
                if (code == 4423) {
                    purchaseResponseModel.setResponseCode(4423);
                    purchaseResponseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.order_not_found));
                } else if (code == 4008) {
                    purchaseResponseModel.setResponseCode(4008);
                    purchaseResponseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.order_completed_or_failed));
                } else if (code == 4009) {
                    purchaseResponseModel.setResponseCode(4009);
                    purchaseResponseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.payment_provider_not_Supported));
                } else if (code == 4010) {
                    purchaseResponseModel.setResponseCode(4010);
                    purchaseResponseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.no_config_found));
                } else if (code == 4302) {
                    purchaseResponseModel.setResponseCode(4302);
                    purchaseResponseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.you_are_logged_out));
                } else if (code == 500) {
                    purchaseResponseModel.setResponseCode(500);
                    purchaseResponseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));
                }
            }

        } catch (Exception ignored) {
            purchaseResponseModel.setResponseCode(500);
            purchaseResponseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));
        }
        return purchaseResponseModel;
    }

    public PurchaseResponseModel updateOrder(Response<PurchaseResponseModel> response) {
        PurchaseResponseModel purchaseResponseModel = new PurchaseResponseModel();
        try {

            JSONObject errorObject = new JSONObject(response.errorBody().string());
            if (errorObject.getInt("responseCode") != 0) {
                int code = errorObject.getInt("responseCode");
                if (code == 4423) {
                    purchaseResponseModel.setResponseCode(4423);
                    purchaseResponseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.order_not_found));
                } else if (code == 4008) {
                    purchaseResponseModel.setResponseCode(4008);
                    purchaseResponseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.order_completed_or_failed));
                } else if (code == 4424) {
                    purchaseResponseModel.setResponseCode(4424);
                    purchaseResponseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.payment_id_not_found));
                } else if (code == 4011) {
                    purchaseResponseModel.setResponseCode(4011);
                    purchaseResponseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.purchase_token_required));
                } else if (code == 4013) {
                    purchaseResponseModel.setResponseCode(4013);
                    purchaseResponseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.payment_validation_failed));
                } else if (code == 4302) {
                    purchaseResponseModel.setResponseCode(4302);
                    purchaseResponseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.you_are_logged_out));
                } else if (code == 4012) {
                    purchaseResponseModel.setResponseCode(4012);
                    purchaseResponseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.receipt_validation_url_not_avail));
                } else if (code == 4010) {
                    purchaseResponseModel.setResponseCode(4010);
                    purchaseResponseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.no_config_found));
                } else if (code == 4009) {
                    purchaseResponseModel.setResponseCode(4009);
                    purchaseResponseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.payment_provider_not_Supported));
                }
            }

        } catch (Exception ignored) {
            purchaseResponseModel.setResponseCode(500);
            purchaseResponseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));
        }
        return purchaseResponseModel;
    }

    public GetContinueWatchingBean continueWatch(Response<GetContinueWatchingBean> response) {
        GetContinueWatchingBean empty = new GetContinueWatchingBean();
        try {
            JSONObject errorObject = new JSONObject(response.errorBody().string());
            if (errorObject.getInt("responseCode") != 0) {
                int code = errorObject.getInt("responseCode");
                if (code == 4302) {
                    empty.setStatus(false);
                    empty.setResponseCode(Long.valueOf(4302));
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.you_are_logged_out));
                } else if (code == 500) {
                    empty.setStatus(false);
                    empty.setResponseCode(Long.valueOf(500));
                    empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));
                }
            }
        } catch (Exception ignored) {
            empty.setResponseCode(Long.valueOf(500));
            empty.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));
        }
        return empty;
    }



    public AllSecondaryAccountDetails allSecondaryAccountDetailsl(Response<AllSecondaryDetails> response) {
        AllSecondaryAccountDetails responseModel = null;
        try {
            responseModel = new AllSecondaryAccountDetails();
            JSONObject errorObject = new JSONObject(response.errorBody().string());
            if (errorObject.getInt("responseCode") != 0) {
                int code = errorObject.getInt("responseCode");
                if (code == 4302) {
                    //  responseModel.setStatus(false);
                    // responseModel.setResponseCode("");
                    responseModel.setDebugMessage("User must be logged in");
                } else   {

                    responseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));
                }
            }

        } catch (Exception e) {

            Logger.e("Exception", String.valueOf(e));

        }

        return responseModel;
    }

    public SecondaryUserDetailsJavaPojo secondaryUserDetails(Response<SecondaryUserDetails> response) {
        SecondaryUserDetailsJavaPojo responseModel = null;
        try {
            responseModel = new SecondaryUserDetailsJavaPojo();
            JSONObject errorObject = new JSONObject(response.errorBody().string());
            if (errorObject.getInt("responseCode") != 0) {
                int code = errorObject.getInt("responseCode");
                if (code == 4302) {
                    //  responseModel.setStatus(false);
                    // responseModel.setResponseCode("");
                    responseModel.setDebugMessage("User must be logged in");
                } else   {

                    responseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));
                }
            }

        } catch (Exception e) {
            Logger.e("Exception", String.valueOf(e));

        }

        return responseModel;
    }

    public SwitchUser switchUserDetails(Response<SwitchUserDetails> response) {
        SwitchUser responseModel = null;
        try {
            responseModel = new SwitchUser();
            JSONObject errorObject = new JSONObject(response.errorBody().string());
            if (errorObject.getInt("responseCode") != 0) {
                int code = errorObject.getInt("responseCode");
                if (code == 4302) {
                  //  responseModel.setStatus(false);
                   // responseModel.setResponseCode("");
                    responseModel.setDebugMessage("User must be logged in");
                } else   {

                    responseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.something_went_wrong_at_our_end));
                }
            }

        } catch (Exception e) {
            Logger.e("Exception", String.valueOf(e));

        }

        return responseModel;
    }




}

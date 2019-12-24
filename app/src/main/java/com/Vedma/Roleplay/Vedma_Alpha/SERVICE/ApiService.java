package com.Vedma.Roleplay.Vedma_Alpha.SERVICE;

import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.AccountInfo;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.ActionAdapter;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.BearerItem;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.Comment;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.GameCharacter;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.DiaryPage;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.Game;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.Ability;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.Invoker;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.GeoLocation;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.MessageView;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.NewsCapture;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.PropertyItem;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.Publisher;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.GeoPosition;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.StatusResponse;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.ArticleView;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.FragmentView;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("api/register")
    Call<String> Register(@Body AccountInfo accountInfo);

    @FormUrlEncoded
    @POST("token")
    Call<BearerItem> TryAuthorize(@Field("grant_type") String grant_type, @Field("username") String login, @Field("password") String password );

    @GET("api/selfInfo")
    Call<AccountInfo> getAccountInfo();

    @POST("api/selfInfo")
    Call<String> setAccountInfo(@Body AccountInfo accountInfo);

    @GET("api/project")
    Call<List<Game>> getGames();

    @POST("api/pushToken")
    Call<String> setPushToken(@Body String pushToken);

    @GET("api/{charId}/main/tag")
    Call<String> getTag(@Path("charId") String charId);

    @GET("api/{charId}/main/status")
    Call<StatusResponse> getStatus(@Path("charId") String charId);

    @GET("api/{charId}/tag")
    Call<Integer> getEntityByTag(@Path("charId") String charId, @Query("tag") String tag);

    @POST("api/{charId}/QRTag")
    Call<String> invokeActionByTag(@Path("charId") String charId, @Query("tag") String tag);

    @GET("api/{charId}/main")
    Call<List<PropertyItem>> getMain(@Path("charId") String charId);

    @GET("api/{charId}/main/outer")
    Call<List<FragmentView>> getOuterMain(@Path("charId") String charId);

    @GET("api/{charId}/news")
    Call<List<NewsCapture>> getNews(@Path("charId") String charId);

    @GET("api/{charId}/news")
    Call<NewsCapture> getArticle(@Path("charId") String charId, @Query("ArticleId") int Id);
    @Multipart
    @POST("api/{charId}/news/publish")
    Call<Integer> postArticle(@Path("charId") String charId,
                              @Query("PublisherId") int PublisherId,
                              @Query("Title") String Title,
                              @Query("Preview")String Preview,
                              @Query("Body") String BodyP,
                              @Part List<MultipartBody.Part> files);

    @POST("api/{charId}/news/publish2")
    Call<Integer> postArticle2(@Path("charId") String charId, @Query("articleId") int articleId,
                             @Body ArticleView article);

    @GET("api/{charId}/news/publishers")
    Call<List<Publisher>> getPublishers(@Path("charId") String charId);

    @GET("api/{charId}/news/{articleId}/comments")
    Call<List<Comment>> getComments(@Path("charId") String charId, @Path("articleId") int articleId);

    @POST("api/{charId}/news/{articleId}/comments")
    Call<String> comment(@Path("charId") String charId, @Path("articleId") int articleId,
                               @Body String comment);
    @GET("api/{charId}/diary")
    Call<List<DiaryPage>> getDiary(@Path("charId") String charId);

    @POST("api/{charId}/diary")
    Call<String> newNote(@Path("charId") String charId, @Body DiaryPage diaryPage);

    @GET("api/{charId}/abilities")
    Call<List<Ability>> getAbilities(@Path("charId") String charId);

    @GET("api/{charId}/abilities")
    Call<ArrayList<ActionAdapter>> getAbilityAdapter(@Path("charId") String charId,
                                                     @Query("abilityId") int abilityId,
                                                     @Query("presetId") int presetId,
                                                     @Query("chainId") int chainId);

    @POST("api/{charId}/abilities")
    Call<String> invokeAbility(@Path("charId") String charId,
                               @Query("abilityId") int abilityId,
                               @Query("presetId") int presetId,
                               @Query("chainId") int chainId,
                               @Body List<Invoker> invokers);

    @GET("api/{charId}/map")
    Call<List<GeoPosition>> getGeoObjects(@Path("charId") String charId);

    @GET("api/{charId}/map")
    Call<ArrayList<ActionAdapter>> getMapActionAdapter(@Path("charId") String charId,
                                                @Query("positionId") int positionId,
                                                @Query("abilityId") int abilityId,
                                                @Query("chainId") int chainId,
                                                @Query("method") GeoPosition.MapMethod method);
    @POST("api/{charId}/map/onclick")
    Call<String> invokeMapClick(@Path("charId") String charId,
                                @Query("positionId") int positionId,
                                @Query("abilityId") int abilityId,
                                @Query("chainId") int chainId,
                                @Body List<Invoker> invokers);
    @POST("api/{charId}/map/oninterract")
    Call<String> invokeMapInteract(@Path("charId") String charId,
                                   @Query("positionId") int positionId,
                                   @Query("abilityId") int abilityId,
                                   @Query("chainId") int chainId,
                                   @Body List<Invoker> invokers);
    @POST("api/{charId}/map/onstep")
    Call<String> invokeMapStep(@Path("charId") String charId,
                                   @Query("positionId") int positionId);

    @POST("api/{charId}/background")
    Call<String> locationPost(@Path("charId") String charId,
                              @Body GeoLocation geoLocation);
    @POST("api/{charId}/logError")
    Call<Void> ErrorPost(@Path("charId") String charId,
                              @Query("message") String error);

    @GET("api/{charId}/contacts")
    Call<List<GameCharacter>> getContacts(@Path("charId") String charId);

    @GET("api/{charId}/main/broadcast")
    Call<MessageView> getBroadcast(@Path("charId") String charId);

    @POST("api/{charId}/contacts")
    Call<Void> postMessage(@Path("charId") String charId, @Body String message);

}

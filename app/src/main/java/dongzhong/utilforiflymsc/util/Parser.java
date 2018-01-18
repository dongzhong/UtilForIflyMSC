package dongzhong.utilforiflymsc.util;

import com.google.gson.Gson;

import java.util.List;

import dongzhong.utilforiflymsc.bean.IatBean;

/**
 * Created by dongzhong on 2018/1/18.
 */

public class Parser {
    /**
     * 将Json格式的识别结果转换为字符串
     *
     * @param iatJsonResult
     * @return
     */
    public static String iatJsonResult2String(String iatJsonResult) {
        String text = "";
        if (iatJsonResult == null) {
            return text;
        }

        StringBuilder stringBuilder = new StringBuilder();
        IatBean iatBean;
        Gson gson = new Gson();
        try {
            iatBean = gson.fromJson(iatJsonResult, IatBean.class);
        }
        catch (Exception e) {
            return text;
        }
        if (iatBean == null) {
            return text;
        }
        if (iatBean.getWs() == null) {
            return text;
        }

        List<IatBean.WsBean> wsBeanList = iatBean.getWs();
        if (wsBeanList == null || wsBeanList.size() == 0) {
            return text;
        }
        for (IatBean.WsBean wsBean : wsBeanList) {
            List<IatBean.WsBean.CwBean> cwBeanList = wsBean.getCw();
            if (cwBeanList == null || cwBeanList.size() == 0) {
                return text;
            }
            for (IatBean.WsBean.CwBean cwBean : cwBeanList) {
                stringBuilder.append(cwBean.getW());
            }
        }
        return stringBuilder.toString();
    }
}

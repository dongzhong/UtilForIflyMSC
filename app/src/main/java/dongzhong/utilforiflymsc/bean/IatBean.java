package dongzhong.utilforiflymsc.bean;

import java.util.List;

/**
 * Created by dongzhong on 2018/1/17.
 */

public class IatBean {
    private int sn;
    private boolean ls;
    private int bg;
    private int ed;
    private List<wsBean> ws;

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public boolean isLs() {
        return ls;
    }

    public void setLs(boolean ls) {
        this.ls = ls;
    }

    public int getBg() {
        return bg;
    }

    public void setBg(int bg) {
        this.bg = bg;
    }

    public int getEd() {
        return ed;
    }

    public void setEd(int ed) {
        this.ed = ed;
    }

    public List<wsBean> getWs() {
        return ws;
    }

    public void setWs(List<wsBean> ws) {
        this.ws = ws;
    }

    public static class wsBean {
        private int bg;
        private List<cwBean> cw;

        public int getBg() {
            return bg;
        }

        public void setBg(int bg) {
            this.bg = bg;
        }

        public List<cwBean> getCw() {
            return cw;
        }

        public void setCw(List<cwBean> cw) {
            this.cw = cw;
        }

        public static class cwBean {
            private String w;
            private int sc;

            public String getW() {
                return w;
            }

            public void setW(String w) {
                this.w = w;
            }

            public int getSc() {
                return sc;
            }

            public void setSc(int sc) {
                this.sc = sc;
            }
        }
    }
}

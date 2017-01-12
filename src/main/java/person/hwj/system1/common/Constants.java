package person.hwj.system1.common;

import java.util.ArrayList;
import java.util.List;

public interface Constants {
    interface FILTER_IGNORE {
        @SuppressWarnings("all")
        List<String> IGNORES = new ArrayList<String>() {
            {
                add("css");
                add("js");
                add("passport");
                add("redirect");
                add("png");
                add("jpg");
                add("gif");
                add("vm");
                add("initUserInfo");
            }
        };

    }


    @SuppressWarnings("all")
    interface PAGE_HELPER {
        final static Integer DEFAULT_PAGE_SIZE = Integer.valueOf(15);
    }
    
    interface ROLE_INFO {
    	final static String YUN_YING_ZHUAN_YUAN = "安全运营专员";
    	final static String DIAO_CHA_ZHU_GUAN = "安全调查主管";
    	final static String DIAO_CHA_ZHU_ZHANG = "安全调查组长";
    	final static String DIAO_CHA_YUAN = "安全调查员";
    	final static String ZHI_JIAN_YUAN = "安全审核专员";
    }
}

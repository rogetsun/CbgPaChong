package com.uv.cbg;

import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author uvsun 2019-08-02 07:14
 */
public class CostPerformanceFilter {

    @Value("#{config['cost_performance.person_score']}")
    private String personCP;
    @Value("#{config['cost_performance.total_score']}")
    private String totalCP;

    private List<CostPerformance> personCPList = new ArrayList<>();
    private boolean hasPersonCP;

    private List<CostPerformance> totalCPList = new ArrayList<>();
    private boolean hasTotalCP;

    public List<CbgGamer> filter(List<CbgGamer> gamerList) {

        if (!hasPersonCP && !hasTotalCP) {
            return null;
        }


        if (null == gamerList || gamerList.size() == 0) {
            return null;
        }

        List<CbgGamer> ret = new ArrayList<>();

        for (CbgGamer gamer : gamerList) {

            if (hasPersonCP) {

                for (CostPerformance cp : personCPList) {

                    if (cp.isCost(new BigDecimal(gamer.getPersonScore()), gamer.getPrice())) {

                        ret.add(gamer);
                        break;

                    }

                }
            }
            if (hasTotalCP) {
                for (CostPerformance cp : totalCPList) {

                    if (cp.isCost(new BigDecimal(gamer.getTotalScore()), gamer.getPrice())) {

                        ret.add(gamer);
                        break;

                    }

                }
            }
        }
        if (ret.size() > 0) {
            return ret;
        }
        return null;
    }

    public void setCP() {
        if (personCP != null && !"".equals(personCP)) {

            String[] pcpArray = personCP.split(",");

            for (String pcp : pcpArray) {
                String[] tmpArr = pcp.split(":");
                personCPList.add(new CostPerformance(new BigDecimal(tmpArr[0]), new BigDecimal(tmpArr[1])));
            }

            if (personCPList.size() > 0) {
                hasPersonCP = true;
            }
        }

        if(totalCP != null && !"".equals(totalCP)){
            String[] pcpArray = totalCP.split(",");

            for (String pcp : pcpArray) {
                String[] tmpArr = pcp.split(":");
                totalCPList.add(new CostPerformance(new BigDecimal(tmpArr[0]), new BigDecimal(tmpArr[1])));
            }

            if (totalCPList.size() > 0) {
                hasTotalCP= true;
            }
        }

    }

    public String getPersonCP() {
        return personCP;
    }

    public void setPersonCP(String personCP) {
        this.personCP = personCP;
    }

    public String getTotalCP() {
        return totalCP;
    }

    public void setTotalCP(String totalCP) {
        this.totalCP = totalCP;
    }
}

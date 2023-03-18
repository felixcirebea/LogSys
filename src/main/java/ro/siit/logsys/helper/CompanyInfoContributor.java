package ro.siit.logsys.helper;

import lombok.Data;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
@Data
public class CompanyInfoContributor implements InfoContributor {
    private Integer totalProfit = 0;
    private LocalDate currentDate = LocalDate.of(2021, 12, 14);

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("company", Map.of(
                "company-profit", String.valueOf(totalProfit),
                "current-date", String.valueOf(currentDate))
        );
    }

    public synchronized void addProfit(Integer value) {
        this.totalProfit += value;
    }

    public void incrementDate() {
        this.currentDate = currentDate.plusDays(1);
    }
}

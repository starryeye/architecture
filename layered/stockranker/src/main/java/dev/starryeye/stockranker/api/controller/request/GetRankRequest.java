package dev.starryeye.stockranker.api.controller.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class GetRankRequest {

    @NotBlank(message = "Tag must not be blank")
    private String tag;

    @Min(value = 1, message = "Size must be at least 1")
    @Max(value = 100, message = "Size must be at most 100")
    private Integer size = 20; // 기본값 20

    public GetRankRequest() {
    }

    public GetRankRequest(String tag, Integer size) {
        this.tag = tag;
        this.size = (size == null) ? 20 : size;
    }

    // setter
    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setSize(Integer size) {
        this.size = (size == null) ? 20 : size;
    }
}

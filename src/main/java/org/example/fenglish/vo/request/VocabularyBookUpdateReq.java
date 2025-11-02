package org.example.fenglish.vo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.Date;

//单词书修改请求VO（前端修改单词书时传递的参数）
@Data
public class VocabularyBookUpdateReq {

    /*单词书名称（可选：若传递则修改名称，不传递则保持原名称）
      约束：若传递，不可为空白字符串*/
    @NotBlank(message = "单词书名称不能为空（若需修改名称）")
    private String bookName;

    //发布时间（可选：若传递则修改发布时间，不传递则保持原时间）
    private Date publishTime;

    public @NotBlank(message = "单词书名称不能为空（若需修改名称）") String getBookName() {
        return bookName;
    }

    public void setBookName(@NotBlank(message = "单词书名称不能为空（若需修改名称）") String bookName) {
        this.bookName = bookName;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }
}
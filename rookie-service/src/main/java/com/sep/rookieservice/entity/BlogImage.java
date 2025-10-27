package com.sep.rookieservice.entity;

import com.sep.rookieservice.enums.IsActived;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "blog_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogImage implements Serializable {
    @Id
    @Column(name = "blog_image_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String blogImageId;

    @NotNull
    @Size(max = 500)
    @Column(name = "image_url", length = 500, nullable = false)
    private String imageUrl;

    @Size(max = 255)
    @Column(name = "alt_text", length = 255)
    private String altText;

    @Column(name = "position")
    private Integer position = 0;

    @Column(name = "blog_id", length = 50)
    private String blogId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "is_actived", nullable = false, length = 10)
    private IsActived isActived = IsActived.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blog_id", referencedColumnName = "blog_id", insertable = false, updatable = false)
    private Blog blog;
}

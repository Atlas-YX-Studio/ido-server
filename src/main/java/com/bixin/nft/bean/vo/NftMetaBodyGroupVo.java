package com.bixin.nft.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NftMetaBodyGroupVo {

	public NftMetaBodyGroupVo(String meta, String body, String payToken, String enDescription) {
		this.meta = meta;
		this.body = body;
		this.payToken = payToken;
	}

	public String meta;

	public String body;

	public String payToken;

	public String enDescription;

	public Set<Long> groupIds = new HashSet<>();

	public Set<Long> elementIds = new HashSet<>();

	public String key() {
		return meta + "::" + body;
	}

}
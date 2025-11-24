package com.example.fenglishandroid.fragment;

import com.example.fenglishandroid.model.VocabularyBookSimpleResp;

public interface OnItemClickListener {
    void onItemClick(VocabularyBookSimpleResp book);
    void onCollectClick(VocabularyBookSimpleResp book);
}
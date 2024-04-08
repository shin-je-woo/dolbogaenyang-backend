package com.whatpl.global.security.permission.manager;

import com.whatpl.global.security.domain.MemberPrincipal;

public interface WhatplPermissionManager {

    boolean hasPrivilege(MemberPrincipal memberPrincipal, Long targetId, String permission);
}

/*
 * Tencent is pleased to support the open source community by making BK-CI 蓝鲸持续集成平台 available.
 *
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * BK-CI 蓝鲸持续集成平台 is licensed under the MIT license.
 *
 * A copy of the MIT License is included in this file.
 *
 *
 * Terms of the MIT License:
 * ---------------------------------------------------
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.tencent.devops.git.service.auth

import com.tencent.devops.git.api.IDevopsApi
import com.tencent.devops.git.exception.ApiException
import com.tencent.devops.git.i18n.GitErrorsText
import com.tencent.devops.git.pojo.AuthInfo
import org.slf4j.LoggerFactory

/**
 * 根据userId获取oauth2 token
 */
class UserTokenGitAuthProvider(
    private val userId: String,
    private val devopsApi: IDevopsApi
) : IGitAuthProvider {

    companion object {
        private val logger = LoggerFactory.getLogger(UserTokenGitAuthProvider::class.java)
    }

    override fun getAuthInfo(): AuthInfo {
        val result = devopsApi.getOauthToken(userId = userId)
        if (result.isNotOk() || result.data == null) {
            logger.error("Fail to get the token($userId) because of ${result.message}")
            throw ApiException(errorMsg = GitErrorsText.get().emptyAccessToken ?: "access token is empty")
        }
        return OauthGitAuthProvider(token = result.data!!.accessToken).getAuthInfo()
    }
}
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

package com.tencent.bk.devops.git.core.service.input

import com.tencent.bk.devops.git.core.enums.ScmType
import com.tencent.bk.devops.git.core.pojo.GitSourceSettings
import com.tencent.bk.devops.git.core.pojo.api.RepositoryType
import com.tencent.bk.devops.git.core.pojo.input.CheckoutAtomParamInput
import com.tencent.bk.devops.git.core.pojo.input.GitCodeAtomParamInput
import com.tencent.bk.devops.git.core.pojo.input.GitCodeCommandAtomParamInput
import com.tencent.bk.devops.git.core.service.helper.IInputAdapter

class CheckoutAtomParamInputAdapter(
    private val input: CheckoutAtomParamInput
) : IInputAdapter {

    override fun getInputs(): GitSourceSettings {
        return when (RepositoryType.valueOf(input.repositoryType)) {
            RepositoryType.ID, RepositoryType.NAME -> {
                input.byRepositoryIdOrName()
            }
            RepositoryType.URL -> {
                input.byRepositoryUrl()
            }
        }
    }

    private fun CheckoutAtomParamInput.byRepositoryIdOrName() = GitCodeAtomParamInputAdapter(
        GitCodeAtomParamInput(
            bkWorkspace = bkWorkspace,
            pipelineId = pipelineId,
            pipelineTaskId = pipelineTaskId,
            pipelineBuildId = pipelineBuildId,
            pipelineStartUserName = pipelineStartUserName,
            postEntryParam = postEntryParam,

            repositoryType = repositoryType,
            repositoryHashId = repositoryHashId,
            repositoryName = repositoryName,
            localPath = localPath,
            strategy = strategy,
            enableSubmodule = enableSubmodule,
            submodulePath = submodulePath,
            enableVirtualMergeBranch = enableVirtualMergeBranch,
            enableSubmoduleRemote = enableSubmoduleRemote,
            enableSubmoduleRecursive = enableSubmoduleRecursive,
            submoduleJobs = submoduleJobs,
            autoCrlf = autoCrlf,
            pullType = pullType,
            branchName = refName,
            tagName = refName,
            commitId = refName,
            includePath = includePath,
            excludePath = excludePath,
            fetchDepth = fetchDepth,
            enableFetchRefSpec = enableFetchRefSpec,
            fetchRefSpec = fetchRefSpec,
            enableGitClean = enableGitClean,
            enableGitCleanIgnore = enableGitCleanIgnore,
            enableGitCleanNested = enableGitCleanNested,
            enableGitLfs = enableGitLfs,
            lfsConcurrentTransfers = lfsConcurrentTransfers,
            enableGitLfsClean = enableGitLfsClean,
            pipelineStartType = pipelineStartType,
            hookEventType = hookEventType,
            hookSourceBranch = hookSourceBranch,
            hookTargetBranch = hookTargetBranch,
            hookSourceUrl = hookSourceUrl,
            hookTargetUrl = hookTargetUrl,
            retryStartPoint = retryStartPoint,
            persistCredentials = persistCredentials,
            compatibleHostList = compatibleHostList,
            enableTrace = enableTrace,
            usernameConfig = usernameConfig,
            userEmailConfig = userEmailConfig,
            enablePartialClone = enablePartialClone,
            cachePath = cachePath,
            enableGlobalInsteadOf = true,
            useCustomCredential = true
        )
    ).getInputs()

    @SuppressWarnings("LongMethod")
    private fun CheckoutAtomParamInput.byRepositoryUrl() = GitCodeCommandAtomParamInputAdapter(
        GitCodeCommandAtomParamInput(
            bkWorkspace = bkWorkspace,
            pipelineId = pipelineId,
            pipelineTaskId = pipelineTaskId,
            pipelineBuildId = pipelineBuildId,
            pipelineStartUserName = pipelineStartUserName,
            postEntryParam = postEntryParam,

            repositoryUrl = repositoryUrl,
            scmType = if (
                repositoryUrl.contains("https://github.com") ||
                repositoryUrl.contains("git@github.com")
            ) {
                ScmType.GITHUB
            } else {
                ScmType.CODE_GIT
            },
            authType = authType,
            ticketId = ticketId,
            accessToken = accessToken,
            username = username,
            password = password,
            personalAccessToken = personalAccessToken,
            authUserId = authUserId,
            localPath = localPath,
            strategy = strategy,
            enableSubmodule = enableSubmodule,
            submodulePath = submodulePath,
            enableVirtualMergeBranch = enableVirtualMergeBranch,
            enableSubmoduleRemote = enableSubmoduleRemote,
            enableSubmoduleRecursive = enableSubmoduleRecursive,
            submoduleJobs = submoduleJobs,
            autoCrlf = autoCrlf,
            pullType = pullType,
            refName = refName,
            includePath = includePath,
            excludePath = excludePath,
            fetchDepth = fetchDepth,
            enableFetchRefSpec = enableFetchRefSpec,
            fetchRefSpec = fetchRefSpec,
            enableGitClean = enableGitClean,
            enableGitCleanIgnore = enableGitCleanIgnore,
            enableGitCleanNested = enableGitCleanNested,
            enableGitLfs = enableGitLfs,
            lfsConcurrentTransfers = lfsConcurrentTransfers,
            enableGitLfsClean = enableGitLfsClean,
            pipelineStartType = pipelineStartType,
            hookEventType = hookEventType,
            hookSourceBranch = hookSourceBranch,
            hookTargetBranch = hookTargetBranch,
            hookSourceUrl = hookSourceUrl,
            hookTargetUrl = hookTargetUrl,
            retryStartPoint = retryStartPoint,
            persistCredentials = persistCredentials,
            hostNameList = compatibleHostList,
            enableTrace = enableTrace,
            usernameConfig = usernameConfig,
            userEmailConfig = userEmailConfig,
            enablePartialClone = enablePartialClone,
            cachePath = cachePath,
            enableGlobalInsteadOf = true,
            useCustomCredential = true
        )
    ).getInputs()
}

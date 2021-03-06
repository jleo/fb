<%@ page import="groovy.json.JsonSlurper; org.Transaction" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'transaction.label', default: 'Transaction')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<a href="#list-transaction" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                                  default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label"
                                                              args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="list-transaction" class="content scaffold-list" role="main">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <table>
        <thead>
        <tr>

            <g:sortableColumn property="matchId" title="${message(code: 'transaction.bet.label', default: 'Bet')}"/>
            <td>A</td>
            <td>B</td>
            <td>联赛</td>
            <td>Display</td>
            <td>Type</td>


            <g:sortableColumn property="delta" title="${message(code: 'transaction.delta.label', default: 'Delta')}"/>

            <g:sortableColumn property="matchId"
                              title="${message(code: 'transaction.matchId.label', default: 'Match Id')}"/>

            <g:sortableColumn property="resultRA"
                              title="${message(code: 'transaction.resultRA.label', default: 'Result RA')}"/>

            <g:sortableColumn property="resultRB"
                              title="${message(code: 'transaction.resultRA.label', default: 'Result RB')}"/>

            <td>Time</td>
        </tr>
        </thead>
        <tbody>
        <g:each in="${transactionInstanceList}" status="i" var="transactionInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                <td><g:link controller="match" action="show"
                            params="${[matchId: transactionInstance.matchId, cid: "18"]}">${fieldValue(bean: transactionInstance, field: "matchId")}</g:link></td>

                <td>${ListParser.parse(transactionInstance.jsonBetInfo.teamA)[1]}</td>
                <td>${ListParser.parse(transactionInstance.jsonBetInfo.teamB)[1]}</td>
                <td>${transactionInstance.jsonBetInfo.mtype[1]}</td>
                <td>${transactionInstance.jsonBetInfo.betOnDisplay}</td>
                <td>${transactionInstance.jsonBetInfo.typeDispaly}</td>

                <td>${fieldValue(bean: transactionInstance, field: "delta")}</td>

                <td>${fieldValue(bean: transactionInstance, field: "matchId")}</td>

                <td>${fieldValue(bean: transactionInstance, field: "resultRA")}</td>
                <td>${fieldValue(bean: transactionInstance, field: "resultRB")}</td>
                <td>${transactionInstance.matchTime.format("yyyy-MM-dd HH:mm")}</td>
                <td><g:if test="${transactionInstance.matchTime.after(new Date())}">
                    未开赛
                </g:if>
                <g:else>
                    <g:if test="${transactionInstance.matchTime.after(new Date(new Date().getTime()-7200*1000))}">
                        进行中
                    </g:if>
                    <g:else>
                        已结束
                    </g:else>
                </g:else>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>

    <div class="pagination">
        <g:paginate total="${transactionInstanceTotal}"/>
    </div>
</div>
</body>
</html>

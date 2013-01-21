<%@ page import="org.Bet" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'bet.label', default: 'Bet')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<a href="#list-bet" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                          default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>

<div id="list-bet" class="content scaffold-list" role="main">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <table>
        <thead>
        <tr>

            <g:sortableColumn property="matchId" title="${message(code: 'bet.matchId.label', default: '比赛id')}"/>


            <g:sortableColumn property="betOn" title="${message(code: 'bet.betOn.label', default: '建议下注')}"/>
            <g:sortableColumn property="ch" title="${message(code: 'bet.betOn.label', default: '盘口')}"/>

            <g:sortableColumn property="teamA" title="${message(code: 'bet.matchId.label', default: '主队')}"/>
            <g:sortableColumn property="teamB" title="${message(code: 'bet.matchId.label', default: '客队')}"/>
            <g:sortableColumn property="h1" title="${message(code: 'bet.matchId.label', default: '主胜')}"/>
            <g:sortableColumn property="h2" title="${message(code: 'bet.matchId.label', default: '客胜')}"/>
            <g:sortableColumn property="matchTime" title="${message(code: 'bet.matchId.label', default: '客队')}"/>

        </tr>
        </thead>
        <tbody>
        <g:each in="${betInstanceList}" status="i" var="betInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                <td><g:link controller="todayMatch" action="show"
                            params="${[matchId: betInstance.matchId, cid: betInstance.cid]}">${betInstance.matchId}</g:link></td>


                <td>${betInstance.betOn==0?"主":"客"}</td>
                <td>${org.HandicapMapping.GoalCn[betInstance.ch]}</td>

                <td>${fieldValue(bean: betInstance, field: "teamA")}</td>
                <td>${fieldValue(bean: betInstance, field: "teamB")}</td>
                <td>${fieldValue(bean: betInstance, field: "h1")}</td>
                <td>${fieldValue(bean: betInstance, field: "h2")}</td>
                <td>${fieldValue(bean: betInstance, field: "matchTime")}</td>
            </tr>
        </g:each>
        </tbody>
    </table>

    <div class="pagination">
        <g:paginate total="${betInstanceTotal}"/>
    </div>
</div>
</body>
</html>

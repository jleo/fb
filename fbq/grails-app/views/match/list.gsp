<%@ page import="org.Match" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'match.label', default: 'Match')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<a href="#list-match" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                            default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label"
                                                              args="[entityName]"/></g:link></li>
    </ul>
</div>
<g:form action="query" name="query">
    <span>比赛Id</span>&nbsp;&nbsp;<g:textField name="matchId" value="${query?.matchId}"></g:textField>
    <span>菠菜公司</span>&nbsp;&nbsp;
    <g:select name="cid" noSelection="${["-1": '']}" from="${CompanyMapping.mapping}"
              optionKey="key"
              optionValue="value" value="${query?.cid}">

    </g:select>
    <g:submitButton name="提交"></g:submitButton>
</g:form>
<div id="list-match" class="content scaffold-list" role="main">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <table>
        <thead>
        <tr>
            <g:sortableColumn property="tNameA" title="主"/>
            <g:sortableColumn property="tNameB" title="客"/>

            <g:sortableColumn property="wr" title="赢"/>
            <g:sortableColumn property="pr" title="平"/>

            <g:sortableColumn property="lr" title="负"/>

            <g:sortableColumn property="matchId" title="比赛场号"/>
            <g:sortableColumn property="h1" title="主胜"/>
            <g:sortableColumn property="h2" title="客胜"/>
            <g:sortableColumn property="resultRA" title="主分"/>
            <g:sortableColumn property="resultRB" title="客分"/>
            <g:sortableColumn property="ch" title="盘口"/>
            <g:sortableColumn property="time" title="时间"/>

        </tr>
        </thead>
        <tbody>
        <g:each in="${matchInstanceList}" status="i" var="matchInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                <td>${fieldValue(bean: matchInstance, field: "tNameA")}</td>
                <td>${fieldValue(bean: matchInstance, field: "tNameB")}</td>
                <td>${fieldValue(bean: matchInstance, field: "w1")}</td>
                <td>${fieldValue(bean: matchInstance, field: "p1")}</td>
                <td>${fieldValue(bean: matchInstance, field: "l1")}</td>
                <td>${fieldValue(bean: matchInstance, field: "matchId")}</td>
                <td>${fieldValue(bean: matchInstance, field: "h1")}</td>
                <td>${fieldValue(bean: matchInstance, field: "h2")}</td>
                <td>${fieldValue(bean: matchInstance, field: "resultRA")}</td>
                <td>${fieldValue(bean: matchInstance, field: "resultRB")}</td>

                <g:if test="${matchInstance.ch}">
                    <td>${org.HandicapMapping.GoalCn[(matchInstance.ch)]}</td>
                </g:if>
                <g:else>
                    <td></td>
                </g:else>
                <td>${matchInstance.time}</td>
            </tr>
        </g:each>
        </tbody>
    </table>

    <div class="pagination">
        <g:paginate total="${matchInstanceTotal}" params="${query}" action="query"/>
    </div>
</div>
</body>
</html>

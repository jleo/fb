package org.jleo

import org.springframework.dao.DataIntegrityViolationException

class TodayMatchController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        params.sort = 'time'
        params.order = "asc"
        [matchInstanceList: TodayMatch.list(params), matchInstanceTotal: TodayMatch.count()]
    }

    def create() {
        [matchInstance: new Match(params)]
    }

    def save() {
        def matchInstance = new Match(params)
        if (!matchInstance.save(flush: true)) {
            render(view: "create", model: [matchInstance: matchInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'TodayMatch.label', default: 'Match'), matchInstance.id])
        redirect(action: "show", id: matchInstance.id)
    }

    def show() {
        def matchInstance = TodayMatch.get(params.id)
        if (!matchInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'TodayMatch.label', default: 'Match'), params.id])
            redirect(action: "list")
            return
        }

        [matchInstance: matchInstance]
    }

    def edit() {
        def matchInstance = TodayMatch.get(params.id)
        if (!matchInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'TodayMatch.label', default: 'Match'), params.id])
            redirect(action: "list")
            return
        }

        [matchInstance: matchInstance]
    }

    def update() {
        def matchInstance = TodayMatch.get(params.id)
        if (!matchInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'TodayMatch.label', default: 'Match'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (matchInstance.version > version) {
                matchInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'TodayMatch.label', default: 'Match')] as Object[],
                        "Another user has updated this Match while you were editing")
                render(view: "edit", model: [matchInstance: matchInstance])
                return
            }
        }

        matchInstance.properties = params

        if (!matchInstance.save(flush: true)) {
            render(view: "edit", model: [matchInstance: matchInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'TodayMatch.label', default: 'Match'), matchInstance.id])
        redirect(action: "show", id: matchInstance.id)
    }

    def delete() {
        def matchInstance = TodayMatch.get(params.id)
        if (!matchInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'TodayMatch.label', default: 'Match'), params.id])
            redirect(action: "list")
            return
        }

        try {
            matchInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'TodayMatch.label', default: 'Match'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'TodayMatch.label', default: 'Match'), params.id])
            redirect(action: "show", id: params.id)
        }
    }

    def query() {
        if (!params.cid && !params.matchId) {
            redirect(action: 'list', params: params)
        }
        def cid = params.cid
        def matchId = params.matchId

        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        params.sort = 'time'
        params.order = "desc"

        if (cid && cid != "-1" && matchId) {
            def count = TodayMatch.countByMatchIdAndCid(matchId, cid)
            render(view: 'list', model: [query: [cid: cid, matchId: matchId],matchInstanceList: TodayMatch.findAllByMatchIdAndCid(matchId, params), matchInstanceTotal: count])
        } else if (cid && cid != -1 && !matchId) {
            def count = TodayMatch.countByCid(cid)
            render(view: 'list', model: [query: [cid: cid],matchInstanceList: TodayMatch.findAllByCid(cid, params), matchInstanceTotal: count])
        }else if((!cid || cid == -1) && !matchId){
            def count = TodayMatch.count()
            render(view: 'list', model: [query: [cid: cid],matchInstanceList: TodayMatch.findAll(params), matchInstanceTotal: count])
        }
        else {
            def count = TodayMatch.countByMatchId(matchId)
            render(view: 'list', model: [query: [cid: cid, matchId: matchId], matchInstanceList: TodayMatch.findAllByMatchId(matchId, params), matchInstanceTotal: count])
        }
    }
}

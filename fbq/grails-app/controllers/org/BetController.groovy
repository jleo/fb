package org

import org.springframework.dao.DataIntegrityViolationException

class BetController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [betInstanceList: Bet.list(params), betInstanceTotal: Bet.count()]
    }

    def create() {
        [betInstance: new Bet(params)]
    }

    def save() {
        def betInstance = new Bet(params)
        if (!betInstance.save(flush: true)) {
            render(view: "create", model: [betInstance: betInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'bet.label', default: 'Bet'), betInstance.id])
        redirect(action: "show", id: betInstance.id)
    }

    def show() {
        def betInstance = Bet.get(params.id)
        if (!betInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'bet.label', default: 'Bet'), params.id])
            redirect(action: "list")
            return
        }

        [betInstance: betInstance]
    }

    def edit() {
        def betInstance = Bet.get(params.id)
        if (!betInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'bet.label', default: 'Bet'), params.id])
            redirect(action: "list")
            return
        }

        [betInstance: betInstance]
    }

    def update() {
        def betInstance = Bet.get(params.id)
        if (!betInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'bet.label', default: 'Bet'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (betInstance.version > version) {
                betInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'bet.label', default: 'Bet')] as Object[],
                          "Another user has updated this Bet while you were editing")
                render(view: "edit", model: [betInstance: betInstance])
                return
            }
        }

        betInstance.properties = params

        if (!betInstance.save(flush: true)) {
            render(view: "edit", model: [betInstance: betInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'bet.label', default: 'Bet'), betInstance.id])
        redirect(action: "show", id: betInstance.id)
    }

    def delete() {
        def betInstance = Bet.get(params.id)
        if (!betInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'bet.label', default: 'Bet'), params.id])
            redirect(action: "list")
            return
        }

        try {
            betInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'bet.label', default: 'Bet'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'bet.label', default: 'Bet'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}

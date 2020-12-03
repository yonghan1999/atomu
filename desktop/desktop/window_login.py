#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from gi.repository import Gtk, Gio, GLib, Gdk

from .api import *
from .window import Window
from .user import set_code, reload_token_async
from .asynchelper import async_function

class LoginWindow(Window):
    def __init__(self, app):
        super().__init__(app, "login")
        self.window.set_application(app)
        self.window.show_all()

    def on_login_clicked(self, button):
        username = self.builder.get_object("username").get_text()
        password = self.builder.get_object("password").get_text()

        def on_reload_token_done(r, e):
            button.set_sensitive(True)
            try:
                result = finish(r, e)
            except:
                pass #FIXME

        def on_done(r, e):
            button.set_sensitive(True)
            try:
                result = finish(r, e)
                set_code(result["uid"], result["auth"])

                button.set_sensitive(False)
                reload_token_async(on_reload_token_done)
            except CSystemError as e:
                self.err("Login failed, please check your username and password.")
            except:
                self.info("Network error, please check your network connection.")

        button.set_sensitive(False)
        api_async("/login/login", {
            "name": username,
            "password": password
        }, on_done)

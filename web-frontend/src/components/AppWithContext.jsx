import React, { useState, useEffect } from "react";
import App from "./App";
import AppContext from "./Context";
import altThemes from "../theme/altThemes";
import { createTheme } from "arwes";

const AppWithContext = () => {
  const [token, setToken] = useState(localStorage.getItem("token"));
  const [user, setUser] = useState("");
  const [id, setId] = useState("");
  const [themeName, setThemeName] = useState(localStorage.getItem("themeName"));
  const [theme, setTheme] = useState({});

  const updateTheme = (themeName) => {
    let myTheme;
    if (themeName === "luke" || !themeName) {
      myTheme = createTheme();
      setTheme(myTheme);
      themeName = "luke";
      setThemeName(themeName);
      localStorage.setItem("themeName", themeName);
    } else {
      myTheme = createTheme(altThemes[themeName]);
      setTheme(myTheme);
      setThemeName(themeName);
      localStorage.setItem("themeName", themeName);
    }
  };

  useEffect(() => {
    if (!themeName) {
      updateTheme();
    } else {
      updateTheme(themeName);
    }
  }, [themeName]);

  useEffect(() => {
    let id = localStorage.getItem("id");
    let isToken = true;
    try {
      isToken = !!JSON.parse(token);
    } catch (err) {
      //do nothing, token is string
    }
    if (id && isToken) {
      (async () => {
        const res = await fetch(
          `${process.env.REACT_APP_BACKEND_URL}/users/${id}`,
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: token,
            },
          }
        );
        const user = await res.json();
        console.log(user);
        setUser(user);
        setId(user.key.$oid);
        localStorage.setItem("user", user);
      })();
    }
  }, [token]);

  const login = (token, user) => {
    console.log(token, user);
    localStorage.setItem("token", token);
    localStorage.setItem("id", user.key.$oid);
    localStorage.setItem("user", JSON.stringify(user));
    setToken(token);
    setUser(user);
    setId(user.key.$oid);
  };
  const logout = () => {
    localStorage.setItem("token", null);
    localStorage.setItem("id", null);
    localStorage.setItem("user", null);
    setToken(null);
    setUser(null);
    setId(null);
  };

  const context = {
    token,
    user,
    id,
    login,
    logout,
    theme,
    themeName,
    updateTheme,
  };
  return (
    <AppContext.Provider value={context}>
      <App />
    </AppContext.Provider>
  );
};

export default AppWithContext;

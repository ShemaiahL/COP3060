"use strict"
console.log("Welcome to PA02 Assigment");
console.log("https://github.com/ShemaiahL/COP3060");

const $ = (sel) => document.querySelector(sel);
const on = (el, evt,fn) => el && el.addEventListener(evt,fn);

function setStatus(message, type = "info") {
    const box = $("#status");
    if(!box) return;
    box.textContext = message;
};

const state = {
    myString: "Rattlers on top",
    myNumber: 33,
    myBool: true,
    myArray: ["Green", "Yellow", "Orange", "Purple", "Pink"],
    myObject: {course: "PA02", ok: true},
    mystery: null,
    notSet: undefined,
};

(function operatorDemo(){
    const total = state.myNumber + 5;
    const exact = (total === 45);
    const ready = state.myBool && exact;
    if (!ready) console.warn("Unexpected fail")
})();

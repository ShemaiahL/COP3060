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

function validateEmail(value){
    const pattern = /^[^\s@]+[^\s@]+\.[^\s@]+$/;

    if(!value || value.trim().length < 5){
        return {ok: false, reason: "Email have minimum of 5 characters."};
    }

    if(!pattern.test(value)){
        return {ok: false, reason: "Please enter a valid email"};
    }
    return {ok: true};
}

function renderTips(){
    const ul = $(#tips-list);
    if(!ul) return;

ul.innerHTML = " ";


for (let i = 0; i < state.myArray.length; i++){
    const li = document.createElement("li");
    li.textContent = ${i+1}. ${state.myArray[i]};
    ul.appendChild(li);
    }
}

function buildUrl(){
    return "https://jsonplaceholder.typicode.com/posts";
}

function pickFields(items){
    return items.map(({id,title,body})=>({id,title,body}));

}

function renderList(list) {
  const mount = $("#results");
  if (!mount) return;

  mount.innerHTML = "";


  if (!list || list.length === 0) {
    mount.innerHTML = `<p>No results found.</p>`;
    return;
  }
  const toShow = list.slice(0, Math.max(10, list.length)); 
    const card = document.createElement("article");

    const h3 = document.createElement("h3");
    h3.textContent = `#${item.id} — ${item.title}`;

    const p = document.createElement("p");
    p.textContent = item.body;

    card.append(h3, p);
    mount.appendChild(card);
  }
}

function sortView(mode = "az") {
  const list = [...state.view]; 

  switch (mode) {
    case "az":
      list.sort((a, b) => a.title.localeCompare(b.title));
      break;
    case "za":
      list.sort((a, b) => b.title.localeCompare(a.title));
      break;
    case "short":
      list.sort((a, b) => a.body.length - b.body.length);
      break;
    case "long":
      list.sort((a, b) => b.body.length - a.body.length);
      break;
    default:
      break;
  }

  state.view = list;
  renderList(state.view);
}


/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Adds a login or logout link to the page, depending on whether the user is
 * already logged in.
 */
function addLoginOrLogoutLinkToHeader() {
	  const navigationElement = document.getElementById('header');
	  if (!navigationElement) {
	    console.warn('Navigation element not found!');
	    return;
	  }
	  fetch('/login-status')
	      .then((response) => {
	        return response.json();
	      })
	      .then((loginStatus) => {
	        if (loginStatus.isLoggedIn) 
	        {
	        	console.warn('add logout');
	        	navigationElement.appendChild(createLink('/logout', 'Logout'));
	        	const urlParams = new URLSearchParams(window.location.search);
	        	const parameterUsername = urlParams.get('user');
	        	const welcomeText = document.getElementById('welcome-user');
	        	welcomeText.innerHTML = "Welcome " + parameterUsername + "!";
	        }
	        else 
	        {
	        	console.warn('add login');
	        	navigationElement.appendChild(createLink('/login', 'Login'));
	        }
	      });
	}

/**
 * Creates an anchor element.
 * @param {string} url
 * @param {string} text
 * @return {Element} Anchor element
 */
function createLink(url, text) {
  const linkElement = document.createElement('a');
  linkElement.appendChild(document.createTextNode(text));
  linkElement.href = url;
  return linkElement;
}

function getQuestionPage() {
	window.location.replace('/question-page.html');
}

function getQandAPage() {
	window.location.replace('/QandA-page.html');
}

/** Fetches messages and add them to the page. */
function fetchQuestions() {
  const urlParams = new URLSearchParams(window.location.search);
  const parameterUsername = urlParams.get('user');
  const url = '/Questions?user=' + parameterUsername;
  fetch(url)
      .then((response) => {
        return response.json();
      })
      .then((messages) => {
        const questionsContainer = document.getElementById('question-container');
        if (messages.length == 0) {
        	questionsContainer.innerHTML = '<p>There is no posts yet.</p>';
        } else {
        	questionsContainer.innerHTML = '';
        }
        messages.forEach((message) => {
          //const messageDiv = buildQuestionDiv(message);
          //questionsContainer.appendChild(messageDiv);
        });
      });
}
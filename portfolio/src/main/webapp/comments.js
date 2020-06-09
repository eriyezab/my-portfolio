const FILTERS = document.getElementById("filters");
const COMMENTS_LIST_DOC_ELEMENT = document.getElementById('comments-list');
const USER_DIV = document.getElementById("users");
const LOG_IN = "Click here to log in: ";
const LOG_OUT = "Click here to log out: ";

function timestampToDate(timestamp) {
  const date = new Date(timestamp);
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const hours = date.getHours();
  const minutes = date.getMinutes();
  const seconds = date.getSeconds();
  const formattedTime = month + '-' + day + '-' + year + ' ' + hours + ':' + minutes + ':' + seconds;
  return formattedTime;
}

function checkCommentPosted() {
  const url = new URL(window.location.href);
  const posted = url.searchParams.get("comment-posted");
  if(posted === "false") {
    window.alert("Comment not posted!");
    console.log("The comment was not posted.");
  }
}

function filterComments() {
  const numComments = FILTERS.querySelector("#num-comments").value;
  const sortValue = FILTERS.querySelector("#sort-value").value;
  const sortOrder = FILTERS.querySelector("#sort-order").value;

  const params = new URLSearchParams();
  params.append("num-comments", numComments);
  params.append("sort-value", sortValue);
  params.append("sort-order", sortOrder);
  
  const url = `/data?${params.toString()}`;
  return fetch(url);
}

function getComments() {
  // Remove comments from page
  removeCommentsFromPage();

  // fetch the data from endpoint to display
  filterComments()
  .then(res => res.json())
  .then((comments) => {
    console.log("Retrieved comments from server.")
    console.log(comments);
    for(i = 0; i < comments.length; ++i) {
      let listNode = document.createElement("LI");
      let displayName;
      if(comments[i].name) {
        displayName = comments[i].name;
      } else {
        displayName = comments[i].email;
      }

      let textNode = 
          document.createTextNode(displayName + 
                                  ' at ' + 
                                  timestampToDate(comments[i].timestamp) + 
                                  ': ' + 
                                  comments[i].message);
      listNode.appendChild(textNode);
      COMMENTS_LIST_DOC_ELEMENT.appendChild(listNode);
    }
  });

  checkCommentPosted();
}

function removeCommentsFromPage() {
  let comments;
  while((comments = COMMENTS_LIST_DOC_ELEMENT.getElementsByTagName("li")).length > 0) {
    COMMENTS_LIST_DOC_ELEMENT.removeChild(comments[0]);
  }
}

function deleteAllComments() {
  deleteComments = confirm("Are you sure you want to delete all comments? This action is irreversible!");
  if(deleteComments) {
    fetch("/delete-comments", {method: "POST"});
    removeCommentsFromPage()
    console.log("Comments deleted.");
  }
}

function getUserStatus() {
  fetch("/user")
  .then(res => res.json())
  .then((userStatus) => {
    if(userStatus.status){
      const commentForm = document.querySelector("form");
      commentForm.removeAttribute("hidden");
      USER_DIV.firstElementChild.innerHTML = LOG_OUT + "<a href='" + userStatus.url + "'>Log Out</a>";
    } else {
      USER_DIV.firstElementChild.innerHTML = LOG_IN + "<a href='" + userStatus.url + "'>Log In</a>";
    }
  });
}

getComments();
getUserStatus();

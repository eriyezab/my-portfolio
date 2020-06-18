const FILTERS = document.getElementById("filters");
const COMMENTS_LIST_DOC_ELEMENT = document.getElementById('comments-list');
const USER_DIV = document.getElementById("users");
const LOG_IN_PROMPT = "Click here to log in: ";
const LOG_OUT_PROMPT = "Click here to log out: ";

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

function getMessageForReason(reason) {
  let message;
  switch (reason) {
    case "score":
      message = "Your sentiment score was too low!";
      break;
    case "empty":
      message = "Your message was empty!";
      break;
    default:
      message = "There was an error posting your comment. Please try again!";
      break;
  }
  return message;
}

function checkCommentPosted() {
  const url = new URL(window.location.href);
  const posted = url.searchParams.get("comment-posted");
  if(posted === "false") {
    const reason = url.searchParams.get("reason");
    const message = getMessageForReason(reason);
    window.alert(message);
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

function createComment(comment) {
  // Create the listnode that will represent a comment.
  const listNode = document.createElement("LI");
  listNode.classList.add("media");
  listNode.classList.add("mt-3");
  listNode.classList.add("mx-3");

  // add the default profile picture and style it.
  const img = document.createElement("IMG");
  img.src = "images/profile_picture.jpg";
  img.height="64";
  img.width="64";
  img.classList.add("img-fluid");
  img.classList.add("img-thumbnail");
  img.classList.add("mr-3");
  listNode.appendChild(img);

  // Add the contents of the comment to the list and style using bootstrap.
  const body = document.createElement("DIV");
  body.classList.add("media-body");

  // The heading will contain the display name of the user and what time they posted it
  const heading = document.createElement("H5");
  const displayName = (comment.name ? comment.name : comment.email);
  const date = timestampToDate(comment.timestamp);
  heading.innerHTML = `${displayName} <small class="text-muted"> at ${date}</small>`;
  heading.classList.add("mt-0");
  heading.classList.add("mb-1");
  body.appendChild(heading);

  // Text will contain the message the user left
  const text = document.createTextNode(`${comment.message} (${comment.sentimentScore})`);
  body.appendChild(text);
  listNode.appendChild(body);
  
  return listNode;
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
    for (i = 0; i < comments.length; ++i) {
      const comment = createComment(comments[i]);
      COMMENTS_LIST_DOC_ELEMENT.appendChild(comment);
    }
  });

  checkCommentPosted();
}

function removeCommentsFromPage() {
  let comments;
  while ((comments = COMMENTS_LIST_DOC_ELEMENT.getElementsByTagName("li")).length > 0) {
    COMMENTS_LIST_DOC_ELEMENT.removeChild(comments[0]);
  }
}

function deleteAllComments() {
  deleteComments = confirm("Are you sure you want to delete all comments? This action is irreversible!");
  if (deleteComments) {
    fetch("/delete-comments", {method: "POST"});
    removeCommentsFromPage();
    console.log("Comments deleted.");
  }
}

function getUserStatus() {
  fetch("/user")
  .then(res => res.json())
  .then((userStatus) => {
    if (userStatus.isLoggedIn) {
      const commentForm = document.querySelector("form");
      commentForm.removeAttribute("hidden");
      USER_DIV.firstElementChild.innerHTML = LOG_OUT_PROMPT + "<a href='" + userStatus.url + "'>Log Out</a>";
    } else {
      USER_DIV.firstElementChild.innerHTML = LOG_IN_PROMPT + "<a href='" + userStatus.url + "'>Log In</a>";
    }
  });
}

function navigateToCommentsSection() {
  const url = new URL(window.location.href);
  const section = url.searchParams.get("section");
  if (section === "comments") {
    // Remove the active class from the about me section of navigation.
    document.querySelector("#navigation #aboutme-nav").classList.remove("active");

    // Add the active class to the comments section of navigation.
    document.querySelector("#navigation #comments-nav").classList.add("active");

    // Remove the active class from the about me section of tab-content.
    document.querySelector(".tab-content #aboutme").classList.remove("active");

    //Add the active class to the comments section of tab-content.
    document.querySelector(".tab-content #comments").classList.add("active");

    // Scroll down to the content section.
    document.getElementById("content").scrollIntoView();
  }
}

getComments();
getUserStatus();
navigateToCommentsSection();

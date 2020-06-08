const COMMENTS_LIST_DOC_ELEMENT = document.getElementById('comments-list');

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

function getComments() {
  fetch("/data")
  .then(res => res.json())
  .then((comments) => {
    console.log("Retrieved comments from server.")
    console.log(comments);
    for(i = 0; i < comments.length; ++i) {
      let listNode = document.createElement("LI");
      let textNode = document.createTextNode(comments[i].name + ' at ' + timestampToDate(comments[i].timestamp) + ': ' + comments[i].message);
      listNode.appendChild(textNode);
      COMMENTS_LIST_DOC_ELEMENT.appendChild(listNode);
    }
  });
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

getComments();



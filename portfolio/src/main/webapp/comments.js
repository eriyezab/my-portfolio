const FILTERS = document.getElementById("filters");

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
  
  //TODO: Remove later
  console.log(params.toString());

  const url = `/data?${params.toString()}`;

  return fetch(url);
}

function getComments() {
  // fetch the data from endpoint to display
  filterComments()
  .then(res => res.json())
  .then((comments) => {
    console.log("Retrieved comments from server.")
    console.log(comments);
    for(i = 0; i < comments.length; ++i) {
      const commentsList = document.getElementById('comments-list');
      let listNode = document.createElement("LI");
      let textNode = 
          document.createTextNode(comments[i].name + 
                                  ' at ' + 
                                  timestampToDate(comments[i].timestamp) + 
                                  ': ' + 
                                  comments[i].message);
      listNode.appendChild(textNode);
      commentsList.appendChild(listNode);
    }
  });

  checkCommentPosted();
}

getComments();

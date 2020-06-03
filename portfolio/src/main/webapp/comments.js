function timestampToDate(timestamp) {
  const date = new Date(timestamp);
  const year = date.getFullYear();
  const month = date.getMonth();
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
      const commentsList = document.getElementById('comments-list');
      let listNode = document.createElement("LI");
      let textNode = document.createTextNode(comments[i].name + ' at ' + timestampToDate(comments[i].timestamp) + ': ' + comments[i].message);
      listNode.appendChild(textNode);
      commentsList.appendChild(listNode);
    }
  });
}

getComments();


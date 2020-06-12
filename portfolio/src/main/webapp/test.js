const displayName = "Anonymous";
const message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris eget ornare eros. Integer tempus, nisi in ornare aliquet, risus turpis porta lacus, id fringilla dui erat a metus. Pellentesque pretium neque egestas urna semper luctus. Proin felis justo, accumsan eu velit vitae, dapibus sagittis nisi. Duis accumsan dictum odio, sit.";
const date = "09-30-19 03:45:21";
const sentimentScore = 0.5;
const COMMENTS_LIST = document.querySelector("#comments ul");


function createTestComments(num) {
  for(let i = 0; i < num; i++) {
    const listNode = document.createElement("LI");
    listNode.classList.add("media");
    listNode.classList.add("mt-3");
    listNode.classList.add("mx-3");
    const img = document.createElement("IMG");
    img.src = "images/profile_picture.jpg";
    img.height="64";
    img.width="64";
    img.classList.add("img-fluid");
    img.classList.add("img-thumbnail");
    img.classList.add("mr-3");
    listNode.appendChild(img);
    const body = document.createElement("DIV");
    body.classList.add("media-body");
    const heading = document.createElement("H5");
    heading.innerHTML = `${displayName} <small class="text-muted"> at ${date}</small>`;
    heading.classList.add("mt-0");
    heading.classList.add("mb-1");
    body.appendChild(heading);
    const text = document.createTextNode(`${message} (${sentimentScore})`);
    body.appendChild(text);
    listNode.appendChild(body);
    COMMENTS_LIST.appendChild(listNode);
  }
}

createTestComments(10);

  


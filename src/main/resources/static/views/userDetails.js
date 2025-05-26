export default async function userDetails({ id }) {
    const response = await fetch(`/api/users/${id}`);
    if (!response.ok) {
        return `
            <div class="text-center">
                <h2>Issue Not Found</h2>
            </div>
        `;
    }

    const user = await response.json();

    return `
        <div class="container mt-5">
            <br />
            <h1 class="mb-4">USER DETAILS</h1>
            <br />
            <div class="border rounded p-4">
                <h2>@${user.username}</h2>
                <br />
                <p>${user.emailAddress || "N/A"}</p>
            </div>
        </div>
    `;
}
